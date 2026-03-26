package dev.uepb.gereciador.ambientes.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.config.AuthConfig;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.GetReservesPerDayRequest;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.entity.User;
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
 * <p>Todos os endpoints requerem autenticação via token JWT. Os endpoints disponíveis são:</p>
 * <ul>
 *   <li>{@code POST /reserves} — solicita uma nova reserva</li>
 *   <li>{@code GET /reserves/{environmentId}?date=} — consulta slots disponíveis por dia</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@RestController
@RequestMapping("/reserves")
@Tag(name = "Reservas", description = "Gerenciamento de reservas de ambientes físicos da UEPB")
@SecurityRequirement(name = "bearerAuth")
public class ReserveController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private ReserveService reserveService;

    /**
     * Solicita uma nova reserva de ambiente para o usuário autenticado.
     *
     * <p>A reserva é criada com status {@code PENDING} e aguarda aprovação de um administrador.
     * Regras aplicadas: data futura, slots de 1 hora em horas cheias (08:00–22:00),
     * sem conflito de horário com reservas existentes.</p>
     *
     * @param createReserveRequest DTO com os dados da reserva solicitada
     * @return {@code 201 Created} com os dados da reserva criada
     */
    @Operation(
        summary = "Criar reserva",
        description = "Solicita a reserva de um ambiente para uma data e horários específicos. "
            + "A reserva é criada com status PENDING e precisa ser aprovada por um administrador. "
            + "Slots devem ter 1 hora de duração, começar em hora cheia, e estar entre 08:00 e 22:00."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso (status: PENDING)",
            content = @Content(schema = @Schema(implementation = Reserve.class))),
        @ApiResponse(responseCode = "400", description = "Data ou slot inválido", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com reserva existente", content = @Content),
        @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Reserve> create(
            @Valid @RequestBody CreateReserveRequest createReserveRequest) {
        User user = (User) authConfig.loadUserByUsername(authConfig.getLoggedUsername());
        Reserve reserve = reserveService.createReserve(user.getId(), createReserveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserve);
    }

    /**
     * Retorna os slots de horário disponíveis (não reservados) para um ambiente em uma data.
     *
     * <p>Os slots cobrem o horário de funcionamento da UEPB (08:00 – 22:00) e são filtrados
     * para exibir apenas os que ainda não possuem reserva na data informada.</p>
     *
     * @param environmentId o ID do ambiente a ser consultado
     * @param date          a data de consulta no formato {@code yyyy-MM-dd}
     * @return {@code 200 OK} com a lista de slots disponíveis
     * @throws ResponseStatusException {@code 400 Bad Request} se a data não for informada
     */
    @Operation(
        summary = "Consultar disponibilidade",
        description = "Retorna os slots de horário disponíveis para reserva em um ambiente específico "
            + "em uma data. O horário de funcionamento considerado é 08:00 às 22:00, com slots de 1 hora."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Slots disponíveis retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Slot.class)))),
        @ApiResponse(responseCode = "400", description = "Data não informada ou inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping("/{environmentId}")
    public ResponseEntity<List<Slot>> getReservesPerDay(
            @Parameter(description = "ID do ambiente", example = "664f1a2b3c4d5e6f7a8b9c0d")
            @PathVariable String environmentId,
            @Parameter(description = "Data de consulta (formato: yyyy-MM-dd)", example = "2026-04-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws ResponseStatusException {

        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data é obrigatória");
        }

        List<Slot> reservesPerDay =
                reserveService.getReservesPerDay(new GetReservesPerDayRequest(date, environmentId));

        return ResponseEntity.ok(reservesPerDay);
    }
}
