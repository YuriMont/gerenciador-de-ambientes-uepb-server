package dev.uepb.gereciador.ambientes.dto.response;

import java.time.Instant;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta com informações de um usuário.
 *
 * <p>Expõe os dados necessários para a interface (identificação, perfil de acesso e data de
 * cadastro), omitindo dados sensíveis como a senha.</p>
 *
 * @param name      nome do usuário
 * @param email     e-mail do usuário
 * @param id        identificador único do usuário
 * @param role      perfil de acesso do usuário
 * @param createdAt data/hora do cadastro
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Dados públicos de um usuário")
public record UserResponse(
    @Schema(description = "Nome completo do usuário", example = "Maria Oliveira")
    String name,

    @Schema(description = "E-mail do usuário", example = "maria.oliveira@uepb.edu.br")
    String email,

    @Schema(description = "Identificador único do usuário", example = "664f1a2b3c4d5e6f7a8b9c0d")
    String id,

    @Schema(description = "Perfil de acesso do usuário", example = "USER")
    UserRole role,

    @Schema(description = "Data/hora do cadastro")
    Instant createdAt
) {

    /**
     * Converte uma entidade {@link User} em seu DTO de resposta.
     *
     * @param user a entidade a ser convertida
     * @return o DTO correspondente, ou {@code null} se {@code user} for {@code null}
     */
    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(user.getName(), user.getEmail(), user.getId(),
                user.getRole() == null ? null : user.getRole().getName(), user.getCreatedAt());
    }
}
