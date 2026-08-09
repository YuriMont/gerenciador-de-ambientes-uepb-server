package dev.uepb.gereciador.ambientes.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dev.uepb.gereciador.ambientes.config.AuthConfig;
import dev.uepb.gereciador.ambientes.dto.response.DashboardResponse;
import dev.uepb.gereciador.ambientes.dto.response.EnvironmentAvailabilityResponse;
import dev.uepb.gereciador.ambientes.dto.response.ReserveResponse;
import dev.uepb.gereciador.ambientes.dto.response.SlotAvailabilityResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.GetReservesPerDayRequest;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import dev.uepb.gereciador.ambientes.service.ReserveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsável pelo gerenciamento de reservas de ambientes.
 *
 * <p>
 * Todos os endpoints requerem autenticação via token JWT. Os endpoints disponíveis são:
 * </p>
 * <ul>
 * <li>{@code POST /reserves} — solicita uma nova reserva</li>
 * <li>{@code GET /reserves} — lista reservas de todos os usuários (ADMIN/OWNER)</li>
 * <li>{@code GET /reserves/mine} — lista as reservas do usuário autenticado</li>
 * <li>{@code GET /reserves/dashboard} — indicadores da tela de início (ADMIN/OWNER)</li>
 * <li>{@code GET /reserves/availability?date=} — agenda do dia de todos os ambientes</li>
 * <li>{@code GET /reserves/{environmentId}?date=} — slots livres de um ambiente no dia</li>
 * <li>{@code GET /reserves/{environmentId}/availability?date=} — agenda completa do ambiente</li>
 * <li>{@code PATCH /reserves/{reserveId}/approve} — aprova uma reserva (ADMIN/OWNER)</li>
 * <li>{@code PATCH /reserves/{reserveId}/reject} — recusa uma reserva (ADMIN/OWNER)</li>
 * <li>{@code DELETE /reserves/{reserveId}} — cancela uma reserva</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@RestController
@RequestMapping("/reserves")
@Tag(name = "Reserves", description = "Gerenciamento de reservas de ambientes físicos da UEPB")
@SecurityRequirement(name = "bearerAuth")
public class ReserveController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private ReserveService reserveService;

    /**
     * Solicita uma nova reserva de ambiente para o usuário autenticado.
     *
     * <p>
     * A reserva é criada com status {@code PENDING} e aguarda aprovação de um administrador. Regras
     * aplicadas: data futura, slots de 1 hora em horas cheias (08:00–22:00), número de
     * participantes dentro da capacidade do ambiente e sem conflito com reservas já aprovadas.
     * </p>
     *
     * @param createReserveRequest DTO com os dados da reserva solicitada
     * @return {@code 201 Created} com os dados da reserva criada
     */
    @Operation(summary = "Criar reserva",
            description = "Solicita a reserva de um ambiente para uma data e horários específicos. "
                    + "A reserva é criada com status PENDING e precisa ser aprovada por um administrador. "
                    + "Slots devem ter 1 hora de duração, começar em hora cheia, e estar entre 08:00 e 22:00. "
                    + "O número de participantes não pode passar da capacidade do ambiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Reserva criada com sucesso (status: PENDING)",
                    content = @Content(schema = @Schema(implementation = Reserve.class))),
            @ApiResponse(responseCode = "400",
                    description = "Data, slot ou número de participantes inválido",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ambiente não encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Horário já confirmado por outra reserva", content = @Content),
            @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<Reserve> create(
            @Valid @RequestBody CreateReserveRequest createReserveRequest) {
        User user = currentUser();
        Reserve reserve = reserveService.createReserve(user.getId(), createReserveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserve);
    }

    /**
     * Lista as reservas de todos os usuários, com filtros opcionais.
     *
     * <p>
     * Acesso restrito a {@code ADMIN} e {@code OWNER}. É a origem da fila de aprovação quando
     * consultado com {@code status=PENDING}.
     * </p>
     *
     * @param status filtro opcional por status
     * @param date filtro opcional por data
     * @param environmentId filtro opcional por ambiente
     * @return {@code 200 OK} com as reservas encontradas
     */
    @Operation(summary = "Listar reservas",
            description = "Retorna as reservas de todos os usuários, com filtros opcionais por status, "
                    + "data e ambiente. Requer perfil ADMIN ou OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas retornadas com sucesso",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ReserveResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado (requer ADMIN ou OWNER)", content = @Content)})
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<ReserveResponse>> findAll(
            @Parameter(description = "Status da reserva",
                    example = "PENDING") @RequestParam(required = false) ReserveStatus status,
            @Parameter(description = "Data da reserva (formato: yyyy-MM-dd)",
                    example = "2026-04-16") @RequestParam(required = false) @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "ID do ambiente") @RequestParam(
                    required = false) String environmentId) {

        return ResponseEntity.ok(reserveService.findAll(status, date, environmentId));
    }

    /**
     * Lista as reservas do usuário autenticado, das mais recentes para as mais antigas.
     *
     * @param status filtro opcional por status
     * @return {@code 200 OK} com as reservas do usuário
     */
    @Operation(summary = "Listar minhas reservas",
            description = "Retorna as reservas solicitadas pelo usuário autenticado, opcionalmente "
                    + "filtradas por status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas retornadas com sucesso",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ReserveResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content)})
    @GetMapping("/mine")
    public ResponseEntity<List<ReserveResponse>> findMine(
            @Parameter(description = "Status da reserva",
                    example = "APPROVED") @RequestParam(required = false) ReserveStatus status) {

        return ResponseEntity.ok(reserveService.findMine(currentUser().getId(), status));
    }

    /**
     * Retorna os indicadores e listas da tela de início.
     *
     * <p>
     * Acesso restrito a {@code ADMIN} e {@code OWNER}, já que o painel reúne dados de todas as
     * pessoas e a fila de aprovação.
     * </p>
     *
     * @return {@code 200 OK} com o painel do dia
     */
    @Operation(summary = "Painel de início",
            description = "Retorna contadores, agenda confirmada do dia, fila de pedidos pendentes e "
                    + "ranking de ambientes mais reservados no mês. Requer perfil ADMIN ou OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Painel retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado (requer ADMIN ou OWNER)", content = @Content)})
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(reserveService.getDashboard());
    }

    /**
     * Retorna a agenda do dia de todos os ambientes cadastrados.
     *
     * @param date a data de consulta; se omitida, usa o dia de hoje
     * @return {@code 200 OK} com a agenda de cada ambiente
     */
    @Operation(summary = "Agenda de todos os ambientes",
            description = "Retorna, para cada ambiente cadastrado, a situação dos 14 horários do dia "
                    + "e a contagem de horários livres. Alimenta a mini-agenda dos cards da tela Ambientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agenda retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(
                            implementation = EnvironmentAvailabilityResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content)})
    @GetMapping("/availability")
    public ResponseEntity<List<EnvironmentAvailabilityResponse>> availabilityForAll(
            @Parameter(description = "Data de consulta (formato: yyyy-MM-dd). Padrão: hoje",
                    example = "2026-04-16") @RequestParam(required = false) @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(reserveService.getAvailabilityForAllEnvironments(targetDate));
    }

    /**
     * Retorna os slots de horário disponíveis (não reservados) para um ambiente em uma data.
     *
     * <p>
     * Os slots cobrem o horário de funcionamento da UEPB (08:00 – 22:00). São omitidos os horários
     * já confirmados e, no dia de hoje, os que já passaram.
     * </p>
     *
     * @param environmentId o ID do ambiente a ser consultado
     * @param date a data de consulta no formato {@code yyyy-MM-dd}
     * @return {@code 200 OK} com a lista de slots disponíveis
     * @throws ResponseStatusException {@code 400 Bad Request} se a data não for informada
     */
    @Operation(summary = "Consultar disponibilidade",
            description = "Retorna os slots de horário disponíveis para reserva em um ambiente específico "
                    + "em uma data. O horário de funcionamento considerado é 08:00 às 22:00, com slots de 1 hora.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Slots disponíveis retornados com sucesso",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = Slot.class)))),
            @ApiResponse(responseCode = "400", description = "Data não informada ou inválida",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content)})
    @GetMapping("/{environmentId}")
    public ResponseEntity<List<Slot>> getReservesPerDay(
            @Parameter(description = "ID do ambiente",
                    example = "664f1a2b3c4d5e6f7a8b9c0d") @PathVariable String environmentId,
            @Parameter(description = "Data de consulta (formato: yyyy-MM-dd)",
                    example = "2026-04-15") @RequestParam @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws ResponseStatusException {

        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data é obrigatória");
        }

        List<Slot> reservesPerDay =
                reserveService.getReservesPerDay(new GetReservesPerDayRequest(date, environmentId));

        return ResponseEntity.ok(reservesPerDay);
    }

    /**
     * Retorna a agenda completa de um ambiente em uma data, com o motivo de cada indisponibilidade.
     *
     * @param environmentId o ID do ambiente a ser consultado
     * @param date a data de consulta; se omitida, usa o dia de hoje
     * @return {@code 200 OK} com os 14 horários do dia e a situação de cada um
     */
    @Operation(summary = "Agenda completa de um ambiente",
            description = "Retorna os 14 horários de 1 hora do dia informando, para cada um, se está "
                    + "livre (AVAILABLE), já confirmado (RESERVED) ou encerrado (CLOSED).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agenda retornada com sucesso",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = SlotAvailabilityResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content)})
    @GetMapping("/{environmentId}/availability")
    public ResponseEntity<List<SlotAvailabilityResponse>> availability(
            @Parameter(description = "ID do ambiente",
                    example = "664f1a2b3c4d5e6f7a8b9c0d") @PathVariable String environmentId,
            @Parameter(description = "Data de consulta (formato: yyyy-MM-dd). Padrão: hoje",
                    example = "2026-04-16") @RequestParam(required = false) @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date == null ? LocalDate.now() : date;
        return ResponseEntity.ok(reserveService.getAvailability(environmentId, targetDate));
    }

    /**
     * Aprova uma reserva pendente, confirmando os horários solicitados.
     *
     * @param reserveId o ID da reserva a ser aprovada
     * @return {@code 200 OK} com a reserva já aprovada
     */
    @Operation(summary = "Aprovar reserva",
            description = "Confirma uma reserva pendente e bloqueia os horários solicitados. "
                    + "Requer perfil ADMIN ou OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva aprovada",
                    content = @Content(schema = @Schema(implementation = ReserveResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado (requer ADMIN ou OWNER)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "Reserva não está pendente ou horário já confirmado",
                    content = @Content)})
    @PatchMapping("/{reserveId}/approve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ReserveResponse> approve(@PathVariable String reserveId) {
        return ResponseEntity.ok(reserveService.approve(reserveId));
    }

    /**
     * Recusa uma reserva pendente, devolvendo os horários à agenda.
     *
     * @param reserveId o ID da reserva a ser recusada
     * @return {@code 200 OK} com a reserva já recusada
     */
    @Operation(summary = "Recusar reserva",
            description = "Recusa uma reserva pendente. Os horários voltam a ficar livres para outras "
                    + "solicitações. Requer perfil ADMIN ou OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva recusada",
                    content = @Content(schema = @Schema(implementation = ReserveResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado (requer ADMIN ou OWNER)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Reserva não está pendente",
                    content = @Content)})
    @PatchMapping("/{reserveId}/reject")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ReserveResponse> reject(@PathVariable String reserveId) {
        return ResponseEntity.ok(reserveService.reject(reserveId));
    }

    /**
     * Cancela uma reserva do usuário autenticado.
     *
     * <p>
     * Administradores podem cancelar a reserva de qualquer pessoa.
     * </p>
     *
     * @param reserveId o ID da reserva a ser cancelada
     */
    @Operation(summary = "Cancelar reserva",
            description = "Remove uma reserva. O solicitante pode cancelar as próprias reservas; "
                    + "ADMIN e OWNER podem cancelar a de qualquer pessoa.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "A reserva é de outra pessoa",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
                    content = @Content)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reserveId}")
    public void cancel(@PathVariable String reserveId) {
        reserveService.cancel(reserveId, currentUser());
    }

    /**
     * Recupera a entidade do usuário autenticado na requisição.
     *
     * @return o usuário autenticado
     */
    private User currentUser() {
        return (User) authConfig.loadUserByUsername(authConfig.getLoggedUsername());
    }
}
