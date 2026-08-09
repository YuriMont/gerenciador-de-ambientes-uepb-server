package dev.uepb.gereciador.ambientes.dto.response;

import java.time.LocalDate;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO com a agenda resumida de um ambiente em uma data.
 *
 * <p>Alimenta a mini-agenda de 14 barras exibida no card de cada ambiente na tela
 * "Ambientes". Devolver a agenda de todos os ambientes de uma vez evita uma
 * requisição por card.</p>
 *
 * @param environmentId identificador do ambiente
 * @param name          nome do ambiente
 * @param date          data consultada
 * @param freeSlots     quantidade de horários livres na data
 * @param totalSlots    quantidade total de horários no dia (14)
 * @param slots         situação de cada horário, em ordem cronológica
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Agenda resumida de um ambiente em uma data")
public record EnvironmentAvailabilityResponse(
    @Schema(description = "Identificador do ambiente")
    String environmentId,

    @Schema(description = "Nome do ambiente", example = "Laboratório de Informática II")
    String name,

    @Schema(description = "Data consultada", example = "2026-04-16")
    LocalDate date,

    @Schema(description = "Horários livres na data", example = "11")
    int freeSlots,

    @Schema(description = "Total de horários do dia", example = "14")
    int totalSlots,

    @Schema(description = "Situação de cada horário do dia")
    List<SlotAvailabilityResponse> slots
) {}
