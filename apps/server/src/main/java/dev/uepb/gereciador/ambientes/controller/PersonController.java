package dev.uepb.gereciador.ambientes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.uepb.gereciador.ambientes.config.AuthConfig;
import dev.uepb.gereciador.ambientes.dto.response.UserResponse;
import dev.uepb.gereciador.ambientes.dto.response.UserSummaryResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável pelo gerenciamento de usuários (pessoas) do sistema.
 *
 * <p>
 * Todos os endpoints requerem autenticação via token JWT. Alguns endpoints possuem restrições
 * adicionais por perfil de acesso:
 * </p>
 * <ul>
 * <li>{@code GET /person/me} — qualquer usuário autenticado</li>
 * <li>{@code GET /person/list} — apenas {@code ADMIN} e {@code OWNER}</li>
 * <li>{@code POST /person/create-admin} — apenas {@code OWNER}</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@RestController
@RequestMapping("person")
@Tag(name = "Users", description = "Gerenciamento de usuários e perfis de acesso")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class PersonController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private PersonService personService;

    /**
     * Retorna os dados do usuário atualmente autenticado.
     *
     * @return {@code 200 OK} com nome e e-mail do usuário logado
     */
    @Operation(summary = "Obter usuário atual",
            description = "Retorna o nome e e-mail do usuário autenticado na requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário retornados",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content)})
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = (User) authConfig.loadUserByUsername(authConfig.getLoggedUsername());
        return ResponseEntity.status(HttpStatus.OK).body(UserResponse.from(user));
    }

    /**
     * Retorna a lista de todos os usuários cadastrados no sistema.
     *
     * <p>
     * Acesso restrito a usuários com perfil {@code ADMIN} ou {@code OWNER}.
     * </p>
     *
     * @return {@code 200 OK} com a lista de todos os usuários
     */
    @Operation(summary = "Listar todos os usuários",
            description = "Retorna nome, e-mail, perfil, data de cadastro e total de reservas de todos "
                    + "os usuários cadastrados. Requer perfil ADMIN ou OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = UserSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado (requer ADMIN ou OWNER)", content = @Content)})
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<UserSummaryResponse>> allUsers() {
        return ResponseEntity.ok(personService.findAllSummaries());
    }

    /**
     * Cria um novo usuário com perfil de administrador ({@code ADMIN}).
     *
     * <p>
     * Acesso exclusivo para usuários com perfil {@code OWNER}.
     * </p>
     *
     * @param registerUserRequest DTO com nome, e-mail e senha do novo administrador
     * @return {@code 200 OK} com os dados do administrador criado
     */
    @Operation(summary = "Criar administrador",
            description = "Cria um novo usuário com perfil ADMIN. Operação exclusiva para OWNER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador criado com sucesso",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer OWNER)",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
                    content = @Content)})
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<User> createAdministrator(
            @RequestBody RegisterUserRequest registerUserRequest) {
        User createdAdmin = personService.createAdministrator(registerUserRequest);
        return ResponseEntity.ok(createdAdmin);
    }
}
