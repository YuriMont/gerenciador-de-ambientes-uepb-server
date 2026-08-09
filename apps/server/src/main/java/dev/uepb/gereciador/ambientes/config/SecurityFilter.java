package dev.uepb.gereciador.ambientes.config;

import java.io.IOException;
import java.util.Optional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de segurança responsável pela validação do token JWT em cada requisição HTTP.
 *
 * <p>Extende {@link OncePerRequestFilter}, garantindo execução única por requisição.
 * O fluxo de validação segue as seguintes etapas:</p>
 * <ol>
 *   <li>Lê o header {@code Authorization} da requisição</li>
 *   <li>Verifica se o valor começa com {@code "Bearer "}</li>
 *   <li>Extrai e valida o token JWT via {@link TokenConfig}</li>
 *   <li>Se válido, carrega os detalhes do usuário e registra a autenticação
 *       no {@link SecurityContextHolder}</li>
 * </ol>
 *
 * <p>Requisições sem token ou com token inválido prosseguem sem autenticação,
 * sendo bloqueadas pelas regras de autorização configuradas em {@link SecurityConfig}.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see TokenConfig
 * @see AuthConfig
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenConfig tokenConfig;

    @Autowired
    private AuthConfig authConfig;

    /**
     * Executa a lógica de validação do token JWT para cada requisição HTTP.
     *
     * @param request     a requisição HTTP recebida
     * @param response    a resposta HTTP a ser enviada
     * @param filterChain a cadeia de filtros seguinte
     * @throws ServletException se ocorrer erro no processamento do servlet
     * @throws IOException      se ocorrer erro de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizedHeader = request.getHeader("Authorization");

        if (!Strings.isEmpty(authorizedHeader) && authorizedHeader.startsWith("Bearer ")) {
            String token = authorizedHeader.substring("Bearer ".length());

            Optional<JWTUserData> optUser = tokenConfig.validateToken(token);

            if (optUser.isPresent()) {
                JWTUserData userData = optUser.get();
                UserDetails userDetails = authConfig.loadUserByUsername(userData.email());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userData, null,
                                userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
