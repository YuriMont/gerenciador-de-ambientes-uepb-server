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

@Component
public class TokenConfig {

    @Value("${app.secret}")
    private String secret;

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(14400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm).build().verify(token);

            return Optional.of(JWTUserData
                    .builder()
                    .userId(decode.getClaim("userId").asLong())
                    .email(decode.getSubject())
                    .build());
        } catch (JWTVerificationException e) {
            // TODO: handle exception
            System.err.println(e.getMessage());

            return Optional.empty();
        }

    }

}
