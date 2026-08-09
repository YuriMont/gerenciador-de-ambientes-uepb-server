package dev.uepb.gereciador.ambientes.dto.resquest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO de requisição para cadastro de um novo usuário.
 *
 * <p>Utilizado tanto no registro público ({@code POST /auth/register})
 * quanto na criação de administradores por um owner ({@code POST /person/create-admin}).</p>
 *
 * @param name     nome completo do usuário
 * @param email    endereço de e-mail único
 * @param password senha de acesso (será armazenada com hash BCrypt)
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Dados para cadastro de um novo usuário")
public record RegisterUserRequest(
    @NotEmpty(message = "Nome é obrigatório")
    @Schema(description = "Nome completo", example = "Carlos Pereira", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    @NotEmpty(message = "Email é obrigatório")
    @Schema(description = "E-mail (deve ser único)", example = "carlos.pereira@uepb.edu.br", requiredMode = Schema.RequiredMode.REQUIRED)
    String email,

    @NotEmpty(message = "Senha é obrigatório")
    @Schema(description = "Senha de acesso", example = "senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    String password
) {}
