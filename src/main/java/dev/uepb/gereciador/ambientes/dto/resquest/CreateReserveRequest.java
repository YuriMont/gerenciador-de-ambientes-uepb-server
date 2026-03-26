package dev.uepb.gereciador.ambientes.dto.resquest;

import java.time.LocalDate;
import java.util.List;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para criação de uma nova reserva de ambiente.
 *
 * <p><strong>Regras de validação:</strong></p>
 * <ul>
 *   <li>A data não pode ser no passado</li>
 *   <li>Cada slot deve ter duração de 1 hora e iniciar em hora cheia</li>
 *   <li>Slots devem estar no intervalo 08:00 – 22:00</li>
 *   <li>Deve haver ao menos 1 slot e 1 participante</li>
 * </ul>
 *
 * @param date                 data da reserva
 * @param environmentId        ID do ambiente a ser reservado
 * @param numberOfParticipants número de participantes (mínimo 1)
 * @param justification        justificativa para a reserva
 * @param slots                lista de slots de horário solicitados
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Dados para solicitação de reserva de um ambiente")
public record CreateReserveRequest(
    @NotNull
    @Schema(description = "Data da reserva (não pode ser no passado)", example = "2026-04-15", requiredMode = Schema.RequiredMode.REQUIRED)
    LocalDate date,

    @NotBlank
    @Schema(description = "ID do ambiente a ser reservado", example = "664f1a2b3c4d5e6f7a8b9c0d", requiredMode = Schema.RequiredMode.REQUIRED)
    String environmentId,

    @NotNull @Min(1)
    @Schema(description = "Número de participantes (mínimo 1)", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer numberOfParticipants,

    @NotBlank
    @Schema(description = "Justificativa para a reserva", example = "Aula prática de Programação Web", requiredMode = Schema.RequiredMode.REQUIRED)
    String justification,

    @NotNull @NotEmpty
    @Schema(description = "Lista de slots de horário (mínimo 1)", requiredMode = Schema.RequiredMode.REQUIRED)
    List<ReserveSlot> slots
) {}
