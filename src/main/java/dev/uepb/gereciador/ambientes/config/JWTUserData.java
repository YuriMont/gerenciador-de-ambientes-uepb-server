package dev.uepb.gereciador.ambientes.config;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {

}
