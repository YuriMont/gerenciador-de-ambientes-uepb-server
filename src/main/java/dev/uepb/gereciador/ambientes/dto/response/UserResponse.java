package dev.uepb.gereciador.ambientes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta com informações de um usuário.
 *
 * <p>Expõe apenas o nome e e-mail, omitindo dados sensíveis como senha e ID interno.</p>
 *
 * @param name  nome do usuário
 * @param email e-mail do usuário
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Dados públicos de um usuário")
public record UserResponse(
    @Schema(description = "Nome completo do usuário", example = "Maria Oliveira")
    String name,

    @Schema(description = "E-mail do usuário", example = "maria.oliveira@uepb.edu.br")
    String email
) {}
