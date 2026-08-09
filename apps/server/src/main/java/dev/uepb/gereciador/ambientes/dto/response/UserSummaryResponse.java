package dev.uepb.gereciador.ambientes.dto.response;

import java.time.Instant;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta com o resumo de um usuário para a tela de administração de pessoas.
 *
 * <p>Acrescenta ao {@link UserResponse} o total de reservas já solicitadas pelo usuário,
 * usado na coluna "RESERVAS" da listagem.</p>
 *
 * @param name         nome do usuário
 * @param email        e-mail do usuário
 * @param id           identificador único do usuário
 * @param role         perfil de acesso do usuário
 * @param createdAt    data/hora do cadastro
 * @param reserveCount quantidade de reservas solicitadas pelo usuário
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Resumo de um usuário com o total de reservas solicitadas")
public record UserSummaryResponse(
    @Schema(description = "Nome completo do usuário", example = "Maria Oliveira")
    String name,

    @Schema(description = "E-mail do usuário", example = "maria.oliveira@uepb.edu.br")
    String email,

    @Schema(description = "Identificador único do usuário", example = "664f1a2b3c4d5e6f7a8b9c0d")
    String id,

    @Schema(description = "Perfil de acesso do usuário", example = "ADMIN")
    UserRole role,

    @Schema(description = "Data/hora do cadastro")
    Instant createdAt,

    @Schema(description = "Total de reservas solicitadas pelo usuário", example = "12")
    long reserveCount
) {

    /**
     * Converte uma entidade {@link User} em seu DTO de resumo.
     *
     * @param user         a entidade a ser convertida
     * @param reserveCount o total de reservas solicitadas pelo usuário
     * @return o DTO correspondente
     */
    public static UserSummaryResponse from(User user, long reserveCount) {
        return new UserSummaryResponse(user.getName(), user.getEmail(), user.getId(),
                user.getRole() == null ? null : user.getRole().getName(), user.getCreatedAt(),
                reserveCount);
    }
}
