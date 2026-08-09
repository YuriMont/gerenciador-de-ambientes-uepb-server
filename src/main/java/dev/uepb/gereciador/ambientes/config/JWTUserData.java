package dev.uepb.gereciador.ambientes.config;

import lombok.Builder;

/**
 * Representa os dados do usuário extraídos de um token JWT após validação.
 *
 * <p>Este record é utilizado como principal ({@code principal}) no contexto de
 * autenticação do Spring Security quando uma requisição é autenticada via token Bearer.</p>
 *
 * @param userId identificador único do usuário no banco de dados
 * @param email  endereço de e-mail do usuário (usado como {@code subject} no JWT)
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Builder
public record JWTUserData(Long userId, String email) {
}
