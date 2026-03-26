package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.DispatcherType;

/**
 * Configuração central do Spring Security para a aplicação.
 *
 * <p>Define a cadeia de filtros de segurança ({@link SecurityFilterChain}), habilitando
 * autenticação stateless via JWT, desabilitando proteção CSRF (adequado para APIs REST)
 * e configurando as regras de autorização por rota.</p>
 *
 * <p><strong>Rotas públicas:</strong></p>
 * <ul>
 *   <li>{@code POST /auth/**} — login e registro de usuários</li>
 *   <li>{@code /swagger-ui/**} e {@code /v3/api-docs/**} — documentação OpenAPI</li>
 *   <li>{@code /h2-console/**} — console H2 (apenas em desenvolvimento)</li>
 * </ul>
 *
 * <p>Todas as demais rotas requerem autenticação via token Bearer JWT.</p>
 *
 * <p>O esquema de segurança {@code bearerAuth} é registrado globalmente para o Swagger UI.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see SecurityFilter
 * @see TokenConfig
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Informe o token JWT obtido no endpoint POST /auth/login. Formato: Bearer {token}"
)
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    /**
     * Configura a cadeia de filtros de segurança HTTP.
     *
     * <p>Aplica as seguintes políticas:</p>
     * <ul>
     *   <li>CSRF desabilitado (API stateless)</li>
     *   <li>CORS configurado via padrões do Spring</li>
     *   <li>Sessão stateless (sem armazenamento de estado no servidor)</li>
     *   <li>Filtro JWT customizado ({@link SecurityFilter}) antes do filtro padrão de autenticação</li>
     * </ul>
     *
     * @param httpSecurity o builder de configuração HTTP do Spring Security
     * @return a cadeia de filtros de segurança configurada
     * @throws Exception se ocorrer erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(httpSecurity))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Expõe o {@link AuthenticationManager} como bean gerenciado pelo Spring.
     *
     * @param authenticationConfiguration a configuração de autenticação do Spring
     * @return o {@link AuthenticationManager} configurado
     * @throws Exception se ocorrer erro ao obter o gerenciador
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define o encoder de senhas usando o algoritmo BCrypt.
     *
     * <p>BCrypt aplica salt automático e é resistente a ataques de força bruta,
     * sendo a escolha recomendada para armazenamento seguro de senhas.</p>
     *
     * @return instância de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
