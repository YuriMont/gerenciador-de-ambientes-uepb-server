package dev.uepb.gereciador.ambientes.dto.resquest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO de requisição para criação ou atualização de um ambiente.
 *
 * <p>Utilizado nos endpoints {@code POST /environments} e {@code PUT /environments/{id}}.</p>
 *
 * @param name        nome do ambiente
 * @param description descrição detalhada do ambiente
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Dados para criação ou atualização de um ambiente")
public record SaveEnvironmentRequest(
    @NotEmpty(message = "Nome é obrigatório")
    @Schema(description = "Nome do ambiente", example = "Laboratório de Informática 03", requiredMode = Schema.RequiredMode.REQUIRED)
    String name,

    @NotEmpty(message = "Descrição é obrigatória")
    @Schema(description = "Descrição do ambiente", example = "Laboratório com 40 computadores, ar-condicionado e projetor", requiredMode = Schema.RequiredMode.REQUIRED)
    String description
) {}
