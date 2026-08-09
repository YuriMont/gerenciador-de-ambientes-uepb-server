package dev.uepb.gereciador.ambientes.config;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.uepb.gereciador.ambientes.entity.User;

/**
 * Componente responsável pela geração e validação de tokens JWT.
 *
 * <p>Utiliza o algoritmo {@code HMAC256} com uma chave secreta configurável via
 * variável de ambiente {@code SECRET} (propriedade {@code app.secret}).</p>
 *
 * <p><strong>Claims do token gerado:</strong></p>
 * <ul>
 *   <li>{@code sub} — e-mail do usuário ({@code subject})</li>
 *   <li>{@code userId} — identificador do usuário no banco de dados</li>
 *   <li>{@code iat} — data/hora de emissão ({@code issuedAt})</li>
 *   <li>{@code exp} — data/hora de expiração (4 horas a partir da emissão)</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see JWTUserData
 */
@Component
public class TokenConfig {

    /** Chave secreta usada na assinatura e verificação do token JWT. */
    @Value("${app.secret}")
    private String secret;

    /**
     * Gera um token JWT assinado para o usuário informado.
     *
     * <p>O token expira em 4 horas (14.400 segundos) a partir do momento da geração.</p>
     *
     * @param user o usuário autenticado para o qual o token será gerado
     * @return a string do token JWT assinado
     */
    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(14400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    /**
     * Valida um token JWT e retorna os dados do usuário extraídos das claims.
     *
     * <p>Retorna {@link Optional#empty()} caso o token seja inválido, expirado
     * ou tenha assinatura incorreta.</p>
     *
     * @param token o token JWT a ser validado
     * @return um {@link Optional} contendo os dados do usuário ({@link JWTUserData}),
     *         ou {@link Optional#empty()} se o token for inválido
     */
    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decode = JWT.require(algorithm).build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(decode.getClaim("userId").asLong())
                    .email(decode.getSubject())
                    .build());
        } catch (JWTVerificationException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
            return Optional.empty();
        }
    }
}
