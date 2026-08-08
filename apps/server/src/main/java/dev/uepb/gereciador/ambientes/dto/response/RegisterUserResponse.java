package dev.uepb.gereciador.ambientes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta para o endpoint de registro de usuário.
 *
 * <p>Retorna os dados básicos do usuário recém-criado para confirmação.</p>
 *
 * @param name  nome do usuário cadastrado
 * @param email e-mail do usuário cadastrado
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Resposta do endpoint de registro com os dados do usuário criado")
public record RegisterUserResponse(
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    String name,

    @Schema(description = "E-mail do usuário", example = "joao.silva@uepb.edu.br")
    String email
) {}
