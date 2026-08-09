package dev.uepb.gereciador.ambientes.dto.resquest;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para consulta de slots disponíveis em um ambiente por dia.
 *
 * <p>Utilizado internamente pelo service para encapsular os parâmetros de consulta
 * recebidos via query string no endpoint {@code GET /reserves/{environmentId}?date=}.</p>
 *
 * @param date          data a ser consultada
 * @param environmentId ID do ambiente
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Parâmetros para consulta de disponibilidade de um ambiente")
public record GetReservesPerDayRequest(
    @NotNull
    @Schema(description = "Data de consulta", example = "2026-04-15")
    LocalDate date,

    @NotBlank
    @Schema(description = "ID do ambiente", example = "664f1a2b3c4d5e6f7a8b9c0d")
    String environmentId
) {}
