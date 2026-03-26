package dev.uepb.gereciador.ambientes.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.service.EnvironmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsável pelo gerenciamento de ambientes físicos da UEPB.
 *
 * <p>Todos os endpoints requerem autenticação via token JWT (Bearer Token).
 * Os endpoints disponíveis são:</p>
 * <ul>
 *   <li>{@code POST /environments} — cria um novo ambiente</li>
 *   <li>{@code PUT /environments/{id}} — atualiza um ambiente existente</li>
 *   <li>{@code GET /environments} — lista todos os ambientes</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@RestController
@RequestMapping("/environments")
@Tag(name = "Ambientes", description = "Gerenciamento dos ambientes físicos disponíveis para reserva")
@SecurityRequirement(name = "bearerAuth")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    /**
     * Cria um novo ambiente no sistema.
     *
     * @param createEnvironmentRequest DTO com nome e descrição do ambiente
     * @return {@code 201 Created} com os dados do ambiente criado
     */
    @Operation(
        summary = "Criar ambiente",
        description = "Cadastra um novo ambiente físico disponível para reserva na UEPB."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ambiente criado com sucesso",
            content = @Content(schema = @Schema(implementation = Environment.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Environment> create(
            @Valid @RequestBody SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = environmentService.create(createEnvironmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(environment);
    }

    /**
     * Atualiza os dados de um ambiente existente.
     *
     * @param environmentId            o ID do ambiente a ser atualizado
     * @param createEnvironmentRequest DTO com os novos dados do ambiente
     * @return {@code 200 OK} com os dados do ambiente atualizado
     */
    @Operation(
        summary = "Atualizar ambiente",
        description = "Atualiza o nome e/ou descrição de um ambiente existente pelo seu ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ambiente atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Environment.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Ambiente não encontrado", content = @Content),
        @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos", content = @Content)
    })
    @PutMapping("/{environmentId}")
    public ResponseEntity<Environment> update(
            @Parameter(description = "ID do ambiente a ser atualizado", example = "664f1a2b3c4d5e6f7a8b9c0d")
            @PathVariable String environmentId,
            @Valid @RequestBody SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = environmentService.update(environmentId, createEnvironmentRequest);
        return ResponseEntity.ok(environment);
    }

    /**
     * Retorna a lista de todos os ambientes cadastrados.
     *
     * @return {@code 200 OK} com a lista de ambientes
     */
    @Operation(
        summary = "Listar ambientes",
        description = "Retorna todos os ambientes físicos cadastrados no sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ambientes retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Environment.class)))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Environment>> findAll() {
        List<Environment> environments = environmentService.findAll();
        return ResponseEntity.ok(environments);
    }
}
