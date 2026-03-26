package dev.uepb.gereciador.ambientes.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.GetReservesPerDayRequest;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import dev.uepb.gereciador.ambientes.repository.ReserveRespository;

/**
 * Serviço responsável pela lógica de negócio relacionada às reservas de ambientes.
 *
 * <p><strong>Regras de negócio aplicadas:</strong></p>
 * <ul>
 *   <li>Reservas só podem ser feitas para datas presentes ou futuras</li>
 *   <li>Cada slot tem duração exata de 1 hora, começando em hora cheia</li>
 *   <li>Horário de funcionamento da UEPB: 08:00 às 22:00</li>
 *   <li>Não é permitido reservar horários que já passaram no dia atual</li>
 *   <li>Não pode haver sobreposição de slots para o mesmo ambiente e data</li>
 *   <li>O status inicial de toda reserva é {@link dev.uepb.gereciador.ambientes.enums.ReserveStatus#PENDING}</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Service
public class ReserveService {

    @Autowired
    private ReserveRespository reserveRespository;

    /** Horário de abertura da UEPB (08:00). */
    private final LocalTime openTimeUEPB = LocalTime.of(8, 0);

    /** Horário de fechamento da UEPB (22:00). */
    private final LocalTime closeTimeUEPB = LocalTime.of(22, 0);

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
     * @throws ResponseStatusException com status {@code 409 Conflict} se houver sobreposição
     *         de horário com reservas já existentes no mesmo ambiente e data
     */
    public Reserve createReserve(String userId, CreateReserveRequest createReserveRequest)
            throws ResponseStatusException {

        if (createReserveRequest.date().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dia inválido");
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

        List<Reserve> reserves = reserveRespository.findAllByEnvironmentIdAndDate(
                createReserveRequest.environmentId(), createReserveRequest.date());

        List<Slot> slots = reserves.stream().flatMap(reserve -> reserve.getSlots().stream()).toList();
        List<ReserveSlot> slotsRequest = createReserveRequest.slots();

        boolean hasTimeConflict = slotsRequest.stream().anyMatch(requestSlot ->
                slots.stream().anyMatch(slot ->
                        requestSlot.startTime().equals(slot.getStartTime())));

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
     * e filtra aqueles que já estão reservados no banco de dados.</p>
     *
     * @param getReservesPerDayRequest DTO com a data e o ID do ambiente a ser consultado
     * @return lista de {@link Slot} disponíveis para reserva na data e ambiente informados
     */
    public List<Slot> getReservesPerDay(GetReservesPerDayRequest getReservesPerDayRequest) {
        List<Reserve> reserves = reserveRespository.findAllByEnvironmentIdAndDate(
                getReservesPerDayRequest.environmentId(), getReservesPerDayRequest.date());

        List<Slot> reservedSlots = reserves.stream()
                .flatMap(reserve -> reserve.getSlots().stream()).toList();

        List<Slot> unreservedSlots = new ArrayList<>();
        LocalTime currentTime = openTimeUEPB;

        while (currentTime.isBefore(closeTimeUEPB)) {
            Slot newSlot = new Slot();
            newSlot.setStartTime(currentTime);
            newSlot.setEndTime(currentTime.plusHours(1));
            unreservedSlots.add(newSlot);
            currentTime = currentTime.plusHours(1);
        }

        return unreservedSlots.stream()
                .filter(unreservedSlot -> reservedSlots.stream()
                        .noneMatch(reservedSlot -> reservedSlot.getStartTime()
                                .equals(unreservedSlot.getStartTime())))
                .toList();
    }
}
