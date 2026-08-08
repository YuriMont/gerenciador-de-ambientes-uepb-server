package dev.uepb.gereciador.ambientes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta para o endpoint de login.
 *
 * <p>Contém o token JWT que deve ser incluído nas requisições subsequentes
 * no header {@code Authorization} no formato {@code Bearer {token}}.</p>
 *
 * @param token o token JWT gerado após autenticação bem-sucedida
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Resposta do endpoint de login contendo o token JWT")
public record LoginResponse(
    @Schema(description = "Token JWT para autenticação nas demais requisições",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token
) {}
