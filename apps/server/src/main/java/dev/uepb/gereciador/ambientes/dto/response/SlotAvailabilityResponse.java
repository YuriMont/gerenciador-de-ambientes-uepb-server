package dev.uepb.gereciador.ambientes.dto.response;

import java.time.LocalTime;
import dev.uepb.gereciador.ambientes.enums.SlotStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que descreve a situação de um slot de 1 hora na agenda de um ambiente.
 *
 * <p>Diferente de {@code GET /reserves/{environmentId}}, que devolve apenas os slots livres,
 * este DTO cobre as 14 horas de funcionamento e informa por que cada horário não está
 * disponível — o que a tela de reserva precisa para desenhar a grade.</p>
 *
 * @param startTime horário de início do slot
 * @param endTime   horário de fim do slot
 * @param status    situação do slot na data consultada
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see SlotStatus
 */
@Schema(description = "Situação de um slot de 1 hora na agenda de um ambiente")
public record SlotAvailabilityResponse(
    @Schema(description = "Horário de início", example = "14:00")
    LocalTime startTime,

    @Schema(description = "Horário de fim", example = "15:00")
    LocalTime endTime,

    @Schema(description = "Situação do slot", example = "AVAILABLE")
    SlotStatus status
) {}
