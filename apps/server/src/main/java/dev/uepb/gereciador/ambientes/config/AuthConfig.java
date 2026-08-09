package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.uepb.gereciador.ambientes.repository.UserRepository;

/**
 * Serviço de autenticação que implementa {@link UserDetailsService} do Spring Security.
 *
 * <p>Responsável por carregar os detalhes de um usuário a partir do banco de dados
 * com base no e-mail, além de fornecer acesso ao usuário atualmente autenticado
 * por meio do {@link SecurityContextHolder}.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Service
public class AuthConfig implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carrega os detalhes de um usuário pelo seu e-mail (usado como {@code username}).
     *
     * @param username o endereço de e-mail do usuário
     * @return os detalhes do usuário ({@link UserDetails})
     * @throws UsernameNotFoundException se nenhum usuário for encontrado com o e-mail informado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    /**
     * Retorna o e-mail do usuário atualmente autenticado na requisição.
     *
     * <p>Extrai a informação do {@link SecurityContextHolder}. Caso o principal não seja
     * uma instância de {@link JWTUserData}, retorna uma string vazia.</p>
     *
     * @return o e-mail do usuário autenticado, ou {@code ""} se não disponível
     */
    public String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object authObject = authentication.getPrincipal();

        if (authObject instanceof JWTUserData) {
            return ((JWTUserData) authObject).email();
        } else {
            return "";
        }
    }
}
