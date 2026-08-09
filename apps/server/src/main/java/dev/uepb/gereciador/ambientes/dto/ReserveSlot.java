package dev.uepb.gereciador.ambientes.dto;

import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO que representa um slot de horário para uma reserva.
 *
 * <p>Cada slot deve ter duração exata de 1 hora e iniciar em hora cheia
 * (ex.: 09:00 – 10:00, 14:00 – 15:00). O horário permitido é entre 08:00 e 22:00.</p>
 *
 * @param startTime horário de início do slot
 * @param endTime   horário de fim do slot
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Slot de horário para reserva (duração de 1 hora, iniciando em hora cheia)")
public record ReserveSlot(
    @NotNull
    @Schema(description = "Horário de início", example = "09:00", type = "string")
    LocalTime startTime,

    @NotNull
    @Schema(description = "Horário de fim (exatamente 1 hora após o início)", example = "10:00", type = "string")
    LocalTime endTime
) {}
