package dev.uepb.gereciador.ambientes.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import dev.uepb.gereciador.ambientes.dto.response.DashboardResponse;
import dev.uepb.gereciador.ambientes.dto.response.EnvironmentAvailabilityResponse;
import dev.uepb.gereciador.ambientes.dto.response.EnvironmentUsageResponse;
import dev.uepb.gereciador.ambientes.dto.response.ReserveResponse;
import dev.uepb.gereciador.ambientes.dto.response.SlotAvailabilityResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.GetReservesPerDayRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import dev.uepb.gereciador.ambientes.enums.SlotStatus;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;
import dev.uepb.gereciador.ambientes.repository.ReserveRespository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;

/**
 * Serviço responsável pela lógica de negócio relacionada às reservas de ambientes.
 *
 * <p><strong>Regras de negócio aplicadas:</strong></p>
 * <ul>
 *   <li>Reservas só podem ser feitas para datas presentes ou futuras</li>
 *   <li>Cada slot tem duração exata de 1 hora, começando em hora cheia</li>
 *   <li>Horário de funcionamento da UEPB: 08:00 às 22:00</li>
 *   <li>Não é permitido reservar horários que já passaram no dia atual</li>
 *   <li>O status inicial de toda reserva é {@link ReserveStatus#PENDING}</li>
 *   <li>Somente reservas {@link ReserveStatus#APPROVED} ocupam um horário: enquanto está
 *       pendente, o slot segue disponível para outras pessoas, e recusar libera o horário</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Service
public class ReserveService {

    @Autowired
    private ReserveRespository reserveRespository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private UserRepository userRepository;

    /** Horário de abertura da UEPB (08:00). */
    private final LocalTime openTimeUEPB = LocalTime.of(8, 0);

    /** Horário de fechamento da UEPB (22:00). */
    private final LocalTime closeTimeUEPB = LocalTime.of(22, 0);

    /**
     * Status que efetivamente ocupam um horário.
     *
     * <p>Apenas a aprovação bloqueia o slot: uma solicitação pendente não impede que outra
     * pessoa peça o mesmo horário, e uma recusa devolve o horário à agenda.</p>
     */
    private static final List<ReserveStatus> OCCUPYING_STATUSES = List.of(ReserveStatus.APPROVED);

    /**
     * Cria uma nova reserva de ambiente para o usuário informado.
     *
     * <p>Valida a data, os slots de horário e verifica conflitos antes de persistir.
     * A reserva é criada com status {@link ReserveStatus#PENDING}.</p>
     *
     * @param userId               o ID do usuário autenticado que está realizando a reserva
     * @param createReserveRequest DTO com os dados da reserva solicitada
     * @return a reserva criada com status PENDING
     * @throws ResponseStatusException com status {@code 400 Bad Request} se a data for inválida
     *         ou algum slot não atender às regras de negócio
     * @throws ResponseStatusException com status {@code 409 Conflict} se algum horário já tiver
     *         uma reserva aprovada no mesmo ambiente e data
     */
    public Reserve createReserve(String userId, CreateReserveRequest createReserveRequest)
            throws ResponseStatusException {

        if (createReserveRequest.date().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dia inválido");
        }

        Environment environment = environmentRepository.findById(createReserveRequest.environmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ambiente não encontrado com ID: " + createReserveRequest.environmentId()));

        // Ambientes cadastrados antes do campo de capacidade não têm o limite definido.
        if (environment.getCapacity() != null
                && createReserveRequest.numberOfParticipants() > environment.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O ambiente tem " + environment.getCapacity() + " lugares");
        }

        createReserveRequest.slots().forEach(slot -> {
            boolean isOneHour = slot.startTime().plusHours(1).equals(slot.endTime());
            boolean startsOnHour = slot.startTime().getMinute() == 0;
            boolean isTimeBeforeNow = createReserveRequest.date().equals(LocalDate.now())
                    && slot.startTime().isBefore(LocalTime.now());

            if (!isOneHour || !startsOnHour || isTimeBeforeNow
                    || slot.startTime().isBefore(openTimeUEPB)
                    || slot.endTime().isAfter(closeTimeUEPB)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot inválido");
            }
        });

        Set<LocalTime> occupied = occupiedStartTimes(createReserveRequest.environmentId(),
                createReserveRequest.date(), null);

        List<ReserveSlot> slotsRequest = createReserveRequest.slots();

        boolean hasTimeConflict =
                slotsRequest.stream().anyMatch(requestSlot -> occupied.contains(requestSlot.startTime()));

        if (hasTimeConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflito de horário");
        }

        Reserve newReserve = new Reserve();
        newReserve.setUserId(userId);
        newReserve.setEnvironmentId(createReserveRequest.environmentId());
        newReserve.setJustification(createReserveRequest.justification());
        newReserve.setNumberOfParticipants(createReserveRequest.numberOfParticipants());
        newReserve.setDate(createReserveRequest.date());
        newReserve.setStatus(ReserveStatus.PENDING);
        newReserve.setSlots(createReserveRequest.slots().stream().map(Slot::new).toList());

        reserveRespository.save(newReserve);
        return newReserve;
    }

    /**
     * Retorna os slots de horário disponíveis (não reservados) para um ambiente em uma data.
     *
     * <p>Gera todos os slots possíveis no horário de funcionamento (08:00 – 22:00, de 1 em 1 hora)
     * e remove os que já têm reserva aprovada, bem como os que já passaram quando a data
     * consultada é hoje.</p>
     *
     * @param getReservesPerDayRequest DTO com a data e o ID do ambiente a ser consultado
     * @return lista de {@link Slot} disponíveis para reserva na data e ambiente informados
     */
    public List<Slot> getReservesPerDay(GetReservesPerDayRequest getReservesPerDayRequest) {
        return getAvailability(getReservesPerDayRequest.environmentId(),
                getReservesPerDayRequest.date()).stream()
                        .filter(slot -> slot.status() == SlotStatus.AVAILABLE).map(slot -> {
                            Slot availableSlot = new Slot();
                            availableSlot.setStartTime(slot.startTime());
                            availableSlot.setEndTime(slot.endTime());
                            return availableSlot;
                        }).toList();
    }

    /**
     * Retorna a situação de cada um dos 14 horários de um ambiente na data informada.
     *
     * <p>Diferente de {@link #getReservesPerDay(GetReservesPerDayRequest)}, devolve também os
     * horários indisponíveis, informando o motivo: {@link SlotStatus#RESERVED} para horários
     * já aprovados e {@link SlotStatus#CLOSED} para horários que já passaram hoje.</p>
     *
     * @param environmentId o ID do ambiente consultado
     * @param date          a data de consulta
     * @return a agenda completa do dia, em ordem cronológica
     */
    public List<SlotAvailabilityResponse> getAvailability(String environmentId, LocalDate date) {
        return buildAvailability(date, occupiedStartTimes(environmentId, date, null));
    }

    /**
     * Retorna a agenda resumida de todos os ambientes em uma data.
     *
     * <p>Feito em uma única consulta de reservas para evitar uma requisição por ambiente
     * na tela "Ambientes".</p>
     *
     * @param date a data de consulta
     * @return a agenda de cada ambiente cadastrado, na ordem em que estão no banco
     */
    public List<EnvironmentAvailabilityResponse> getAvailabilityForAllEnvironments(LocalDate date) {
        Map<String, Set<LocalTime>> occupiedByEnvironment =
                reserveRespository.findAllByDateAndStatusIn(date, OCCUPYING_STATUSES).stream()
                        .collect(Collectors.groupingBy(Reserve::getEnvironmentId,
                                Collectors.flatMapping(
                                        reserve -> reserve.getSlots().stream().map(Slot::getStartTime),
                                        Collectors.toSet())));

        return environmentRepository.findAll().stream().map(environment -> {
            List<SlotAvailabilityResponse> slots = buildAvailability(date,
                    occupiedByEnvironment.getOrDefault(environment.getId(), Set.of()));
            int free = (int) slots.stream().filter(slot -> slot.status() == SlotStatus.AVAILABLE).count();
            return new EnvironmentAvailabilityResponse(environment.getId(), environment.getName(),
                    date, free, slots.size(), slots);
        }).toList();
    }

    /**
     * Retorna as reservas do usuário informado, das mais recentes para as mais antigas.
     *
     * @param userId o ID do solicitante
     * @param status filtro opcional de status; {@code null} devolve todas
     * @return lista de reservas do usuário já enriquecidas com ambiente e solicitante
     */
    public List<ReserveResponse> findMine(String userId, ReserveStatus status) {
        List<Reserve> reserves = reserveRespository.findAllByUserIdOrderByDateDesc(userId).stream()
                .filter(reserve -> status == null || reserve.getStatus() == status).toList();

        return toResponses(reserves);
    }

    /**
     * Retorna as reservas de todos os usuários, com filtros opcionais.
     *
     * <p>Pendentes são devolvidas das mais antigas para as mais recentes (ordem da fila de
     * aprovação); as demais consultas seguem a ordem de data decrescente.</p>
     *
     * @param status        filtro opcional de status
     * @param date          filtro opcional de data
     * @param environmentId filtro opcional de ambiente
     * @return lista de reservas já enriquecidas com ambiente e solicitante
     */
    public List<ReserveResponse> findAll(ReserveStatus status, LocalDate date,
            String environmentId) {

        List<Reserve> reserves = status == ReserveStatus.PENDING
                ? reserveRespository.findAllByStatusOrderByCreatedAtAsc(ReserveStatus.PENDING)
                : reserveRespository.findAll();

        List<Reserve> filtered = reserves.stream()
                .filter(reserve -> status == null || reserve.getStatus() == status)
                .filter(reserve -> date == null || date.equals(reserve.getDate()))
                .filter(reserve -> environmentId == null
                        || environmentId.equals(reserve.getEnvironmentId()))
                .sorted(status == ReserveStatus.PENDING ? Comparator.comparing(Reserve::getDate)
                        : Comparator.comparing(Reserve::getDate).reversed())
                .toList();

        return toResponses(filtered);
    }

    /**
     * Aprova uma reserva pendente, confirmando os horários solicitados.
     *
     * @param reserveId o ID da reserva a ser aprovada
     * @return a reserva aprovada, já enriquecida com ambiente e solicitante
     * @throws ResponseStatusException {@code 404 Not Found} se a reserva não existir
     * @throws ResponseStatusException {@code 409 Conflict} se a reserva não estiver pendente ou
     *         se algum horário já tiver sido confirmado por outra reserva
     */
    public ReserveResponse approve(String reserveId) {
        Reserve reserve = findByIdOrThrow(reserveId);

        if (reserve.getStatus() != ReserveStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível aprovar uma reserva pendente");
        }

        Set<LocalTime> occupied =
                occupiedStartTimes(reserve.getEnvironmentId(), reserve.getDate(), reserve.getId());

        boolean hasTimeConflict =
                reserve.getSlots().stream().anyMatch(slot -> occupied.contains(slot.getStartTime()));

        if (hasTimeConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Outro pedido já confirmou um dos horários solicitados");
        }

        reserve.setStatus(ReserveStatus.APPROVED);
        reserveRespository.save(reserve);

        return toResponses(List.of(reserve)).get(0);
    }

    /**
     * Recusa uma reserva pendente, devolvendo os horários à agenda.
     *
     * @param reserveId o ID da reserva a ser recusada
     * @return a reserva recusada, já enriquecida com ambiente e solicitante
     * @throws ResponseStatusException {@code 404 Not Found} se a reserva não existir
     * @throws ResponseStatusException {@code 409 Conflict} se a reserva não estiver pendente
     */
    public ReserveResponse reject(String reserveId) {
        Reserve reserve = findByIdOrThrow(reserveId);

        if (reserve.getStatus() != ReserveStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível recusar uma reserva pendente");
        }

        reserve.setStatus(ReserveStatus.REJECTED);
        reserveRespository.save(reserve);

        return toResponses(List.of(reserve)).get(0);
    }

    /**
     * Cancela uma reserva, removendo-a do sistema.
     *
     * <p>O solicitante pode cancelar as próprias reservas; {@code ADMIN} e {@code OWNER} podem
     * cancelar a de qualquer pessoa.</p>
     *
     * @param reserveId o ID da reserva a ser cancelada
     * @param user      o usuário autenticado que está cancelando
     * @throws ResponseStatusException {@code 404 Not Found} se a reserva não existir
     * @throws ResponseStatusException {@code 403 Forbidden} se a reserva for de outra pessoa e o
     *         usuário não for administrador
     */
    public void cancel(String reserveId, User user) {
        Reserve reserve = findByIdOrThrow(reserveId);

        UserRole role = user.getRole() == null ? null : user.getRole().getName();
        boolean isAdmin = role == UserRole.ADMIN || role == UserRole.OWNER;

        if (!isAdmin && !user.getId().equals(reserve.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Só é possível cancelar as próprias reservas");
        }

        reserveRespository.delete(reserve);
    }

    /**
     * Monta os indicadores e listas da tela de início.
     *
     * @return o painel com contadores, agenda do dia, fila de pendentes e ranking do mês
     */
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();

        List<Reserve> pending =
                reserveRespository.findAllByStatusOrderByCreatedAtAsc(ReserveStatus.PENDING);

        List<Reserve> todayApproved =
                reserveRespository.findAllByDateAndStatusIn(today, OCCUPYING_STATUSES).stream()
                        .sorted(Comparator.comparing(this::firstStartTime)).toList();

        long environmentCount = environmentRepository.count();

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        long weekHours = countApprovedHours(weekStart, weekEnd);
        long weekCapacity = environmentCount * slotsPerDay() * 7L;
        int weeklyOccupancyRate =
                weekCapacity == 0 ? 0 : (int) Math.round(weekHours * 100.0 / weekCapacity);

        return new DashboardResponse(pending.size(), todayApproved.size(), environmentCount,
                weeklyOccupancyRate, toResponses(todayApproved), toResponses(pending),
                topEnvironmentsOfMonth(today));
    }

    /**
     * Retorna os horários já ocupados por reservas aprovadas em um ambiente e data.
     *
     * @param environmentId  o ID do ambiente
     * @param date           a data consultada
     * @param ignoreReserveId reserva a ser desconsiderada (a própria, ao aprovar); pode ser null
     * @return conjunto de horários de início ocupados
     */
    private Set<LocalTime> occupiedStartTimes(String environmentId, LocalDate date,
            String ignoreReserveId) {
        return reserveRespository
                .findAllByEnvironmentIdAndDateAndStatusIn(environmentId, date, OCCUPYING_STATUSES)
                .stream().filter(reserve -> !reserve.getId().equals(ignoreReserveId))
                .flatMap(reserve -> reserve.getSlots().stream()).map(Slot::getStartTime)
                .collect(Collectors.toSet());
    }

    /**
     * Gera a agenda completa de um dia marcando cada horário como livre, reservado ou encerrado.
     *
     * @param date     a data da agenda
     * @param occupied os horários de início já confirmados
     * @return a lista dos 14 slots do dia, em ordem cronológica
     */
    private List<SlotAvailabilityResponse> buildAvailability(LocalDate date,
            Set<LocalTime> occupied) {
        List<SlotAvailabilityResponse> slots = new ArrayList<>();
        LocalTime currentTime = openTimeUEPB;
        boolean isToday = date.equals(LocalDate.now());

        while (currentTime.isBefore(closeTimeUEPB)) {
            SlotStatus status;

            if (occupied.contains(currentTime)) {
                status = SlotStatus.RESERVED;
            } else if (date.isBefore(LocalDate.now())
                    || (isToday && currentTime.isBefore(LocalTime.now()))) {
                status = SlotStatus.CLOSED;
            } else {
                status = SlotStatus.AVAILABLE;
            }

            slots.add(new SlotAvailabilityResponse(currentTime, currentTime.plusHours(1), status));
            currentTime = currentTime.plusHours(1);
        }

        return slots;
    }

    /**
     * Enriquece as reservas com o nome do ambiente e os dados do solicitante.
     *
     * <p>Os ambientes e usuários referenciados são buscados em duas consultas, independente
     * da quantidade de reservas.</p>
     *
     * @param reserves as reservas a converter
     * @return a lista de DTOs, na mesma ordem recebida
     */
    private List<ReserveResponse> toResponses(List<Reserve> reserves) {
        if (reserves.isEmpty()) {
            return List.of();
        }

        Set<String> environmentIds =
                reserves.stream().map(Reserve::getEnvironmentId).collect(Collectors.toSet());
        Set<String> userIds = reserves.stream().map(Reserve::getUserId).collect(Collectors.toSet());

        Map<String, Environment> environments = environmentRepository.findAllById(environmentIds)
                .stream().collect(Collectors.toMap(Environment::getId, Function.identity()));
        Map<String, User> users = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return reserves.stream()
                .map(reserve -> ReserveResponse.from(reserve,
                        environments.get(reserve.getEnvironmentId()),
                        users.get(reserve.getUserId())))
                .toList();
    }

    /**
     * Busca uma reserva pelo ID ou lança {@code 404 Not Found}.
     *
     * @param reserveId o ID da reserva
     * @return a reserva encontrada
     */
    private Reserve findByIdOrThrow(String reserveId) {
        return reserveRespository.findById(reserveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva não encontrada com ID: " + reserveId));
    }

    /**
     * Soma as horas confirmadas em um intervalo de datas.
     *
     * @param start data inicial (inclusive)
     * @param end   data final (inclusive)
     * @return o total de horas aprovadas no intervalo
     */
    private long countApprovedHours(LocalDate start, LocalDate end) {
        return reserveRespository.findAllByDateBetween(start, end).stream()
                .filter(reserve -> reserve.getStatus() == ReserveStatus.APPROVED)
                .mapToLong(reserve -> reserve.getSlots().size()).sum();
    }

    /**
     * Monta o ranking dos cinco ambientes com mais horas confirmadas no mês da data informada.
     *
     * @param reference data usada para delimitar o mês
     * @return os ambientes mais reservados, do maior para o menor total de horas
     */
    private List<EnvironmentUsageResponse> topEnvironmentsOfMonth(LocalDate reference) {
        LocalDate monthStart = reference.withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        Map<String, Integer> hoursByEnvironment = new LinkedHashMap<>();

        reserveRespository.findAllByDateBetween(monthStart, monthEnd).stream()
                .filter(reserve -> reserve.getStatus() == ReserveStatus.APPROVED)
                .forEach(reserve -> hoursByEnvironment.merge(reserve.getEnvironmentId(),
                        reserve.getSlots().size(), Integer::sum));

        Map<String, Environment> environments =
                environmentRepository.findAllById(hoursByEnvironment.keySet()).stream()
                        .collect(Collectors.toMap(Environment::getId, Function.identity()));

        return hoursByEnvironment.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(5)
                .map(entry -> new EnvironmentUsageResponse(entry.getKey(),
                        environments.containsKey(entry.getKey())
                                ? environments.get(entry.getKey()).getName()
                                : null,
                        entry.getValue()))
                .toList();
    }

    /**
     * Retorna o horário de início mais cedo de uma reserva, usado para ordenar a agenda do dia.
     *
     * @param reserve a reserva consultada
     * @return o menor horário de início, ou {@link #openTimeUEPB} se não houver slots
     */
    private LocalTime firstStartTime(Reserve reserve) {
        return reserve.getSlots().stream().map(Slot::getStartTime).min(Comparator.naturalOrder())
                .orElse(openTimeUEPB);
    }

    /**
     * Quantidade de slots de 1 hora que cabem no horário de funcionamento.
     *
     * @return o total de slots do dia (14)
     */
    private int slotsPerDay() {
        return closeTimeUEPB.getHour() - openTimeUEPB.getHour();
    }
}
