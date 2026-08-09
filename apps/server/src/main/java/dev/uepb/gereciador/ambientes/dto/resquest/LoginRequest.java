package dev.uepb.gereciador.ambientes.dto.resquest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO de requisição para o endpoint de login.
 *
 * @param email    e-mail do usuário cadastrado
 * @param password senha do usuário
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Credenciais para autenticação")
public record LoginRequest(
    @NotEmpty(message = "Email é obrigatório")
    @Schema(description = "E-mail do usuário", example = "joao.silva@uepb.edu.br", requiredMode = Schema.RequiredMode.REQUIRED)
    String email,

    @NotEmpty(message = "Senha é obrigatório")
    @Schema(description = "Senha do usuário", example = "senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    String password
) {}
