package dev.uepb.gereciador.ambientes.dto.resquest;

import jakarta.validation.constraints.NotEmpty;

public record CreateEnvironmentRequest(
    @NotEmpty(message = "Nome é obrigatório") String name,
    @NotEmpty(message = "Descrição é obrigatória") String description
) {}
