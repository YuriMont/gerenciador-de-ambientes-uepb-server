package dev.uepb.gereciador.ambientes.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta de uma reserva já enriquecido com os dados do ambiente e do solicitante.
 *
 * <p>A interface precisa exibir o nome do ambiente e da pessoa em todas as listagens
 * (agenda do dia, minhas reservas e fila de aprovação). Resolver essas referências no
 * servidor evita que o cliente faça uma requisição por linha.</p>
 *
 * @param id                   identificador da reserva
 * @param environmentId        identificador do ambiente reservado
 * @param environmentName      nome do ambiente reservado
 * @param environmentBlock     bloco onde o ambiente fica
 * @param environmentCapacity  quantidade de lugares do ambiente
 * @param userId               identificador do solicitante
 * @param userName             nome do solicitante
 * @param userEmail            e-mail do solicitante
 * @param date                 data da reserva
 * @param slots                slots de horário solicitados
 * @param numberOfParticipants número de participantes previstos
 * @param justification        justificativa informada pelo solicitante
 * @param status               status atual da reserva
 * @param createdAt            data/hora do envio da solicitação
 * @param updatedAt            data/hora da última atualização
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Reserva com os dados do ambiente e do solicitante já resolvidos")
public record ReserveResponse(
    @Schema(description = "Identificador da reserva")
    String id,

    @Schema(description = "Identificador do ambiente reservado")
    String environmentId,

    @Schema(description = "Nome do ambiente reservado", example = "Auditório Central – Reitoria")
    String environmentName,

    @Schema(description = "Bloco onde o ambiente fica", example = "Bloco C")
    String environmentBlock,

    @Schema(description = "Quantidade de lugares do ambiente", example = "220")
    Integer environmentCapacity,

    @Schema(description = "Identificador do solicitante")
    String userId,

    @Schema(description = "Nome do solicitante", example = "Marcelo Pontes")
    String userName,

    @Schema(description = "E-mail do solicitante", example = "marcelo.pontes@uepb.edu.br")
    String userEmail,

    @Schema(description = "Data da reserva", example = "2026-04-16")
    LocalDate date,

    @Schema(description = "Slots de horário solicitados")
    List<Slot> slots,

    @Schema(description = "Número de participantes", example = "180")
    Integer numberOfParticipants,

    @Schema(description = "Justificativa da reserva")
    String justification,

    @Schema(description = "Status da reserva", example = "PENDING")
    ReserveStatus status,

    @Schema(description = "Data/hora do envio da solicitação")
    Instant createdAt,

    @Schema(description = "Data/hora da última atualização")
    Instant updatedAt
) {

    /**
     * Monta o DTO a partir da reserva e das entidades relacionadas.
     *
     * @param reserve     a reserva de origem
     * @param environment o ambiente reservado; pode ser {@code null} se tiver sido excluído
     * @param user        o solicitante; pode ser {@code null} se tiver sido excluído
     * @return o DTO correspondente
     */
    public static ReserveResponse from(Reserve reserve, Environment environment, User user) {
        return new ReserveResponse(
                reserve.getId(),
                reserve.getEnvironmentId(),
                environment == null ? null : environment.getName(),
                environment == null ? null : environment.getBlock(),
                environment == null ? null : environment.getCapacity(),
                reserve.getUserId(),
                user == null ? null : user.getName(),
                user == null ? null : user.getEmail(),
                reserve.getDate(),
                reserve.getSlots(),
                reserve.getNumberOfParticipants(),
                reserve.getJustification(),
                reserve.getStatus(),
                reserve.getCreatedAt(),
                reserve.getUpdatedAt());
    }
}
