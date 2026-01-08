package dev.uepb.gereciador.ambientes.dto.resquest;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "Email é obrigatório") String email,
        @NotEmpty(message = "Senha é obrigratório") String password) {

}
