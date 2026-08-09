package dev.uepb.gereciador.ambientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.uepb.gereciador.ambientes.config.TokenConfig;
import dev.uepb.gereciador.ambientes.dto.response.LoginResponse;
import dev.uepb.gereciador.ambientes.dto.response.RegisterUserResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.LoginRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsável pelos endpoints públicos de autenticação e registro de usuários.
 *
 * <p>
 * Não requer autenticação prévia. Os endpoints disponíveis são:
 * </p>
 * <ul>
 * <li>{@code POST /auth/login} — autentica credenciais e retorna um token JWT</li>
 * <li>{@code POST /auth/register} — registra um novo usuário com perfil USER</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints públicos para login e registro de usuários")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenConfig tokenConfig;

    /**
     * Autentica um usuário com e-mail e senha e retorna um token JWT.
     *
     * <p>
     * O token gerado tem validade de 4 horas e deve ser incluído nas demais requisições
     * autenticadas no header {@code Authorization: Bearer {token}}.
     * </p>
     *
     * @param request DTO com e-mail e senha do usuário
     * @return {@code 200 OK} com o token JWT em caso de sucesso
     */
    @Operation(summary = "Realizar login",
            description = "Autentica o usuário com e-mail e senha. Retorna um token JWT válido por 4 horas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos",
                    content = @Content)})
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(userAndPass);
        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Registra um novo usuário com perfil {@code USER} no sistema.
     *
     * <p>
     * O e-mail deve ser único. A senha é armazenada com hash BCrypt.
     * </p>
     *
     * @param request DTO com nome, e-mail e senha do novo usuário
     * @return {@code 201 Created} com nome e e-mail do usuário criado
     */
    @Operation(summary = "Registrar novo usuário",
            description = "Cria um novo usuário com perfil USER. O e-mail deve ser único no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserResponse.class))),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos",
                    content = @Content)})
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Role role = roleRepository.findByName(UserRole.USER).orElseThrow();

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setRole(role);

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(newUser.getName(), newUser.getEmail()));
    }
}
