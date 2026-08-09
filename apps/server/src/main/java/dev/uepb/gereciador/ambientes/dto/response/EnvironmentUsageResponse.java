package dev.uepb.gereciador.ambientes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO com o total de horas aprovadas de um ambiente em um período.
 *
 * <p>Alimenta o ranking "Mais reservados no mês" da tela de início.</p>
 *
 * @param environmentId identificador do ambiente
 * @param name          nome do ambiente
 * @param hours         horas confirmadas no período
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Total de horas confirmadas de um ambiente em um período")
public record EnvironmentUsageResponse(
    @Schema(description = "Identificador do ambiente")
    String environmentId,

    @Schema(description = "Nome do ambiente", example = "Laboratório de Informática II")
    String name,

    @Schema(description = "Horas confirmadas no período", example = "62")
    int hours
) {}
