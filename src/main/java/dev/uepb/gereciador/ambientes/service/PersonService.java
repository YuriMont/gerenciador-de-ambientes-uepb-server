package dev.uepb.gereciador.ambientes.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;

/**
 * Serviço responsável pela lógica de negócio relacionada à gestão de usuários (pessoas).
 *
 * <p>
 * Provê operações de consulta e criação de usuários, incluindo a criação especial de
 * administradores, que só pode ser executada por um owner do sistema.
 * </p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Service
public class PersonService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retorna a lista de todos os usuários cadastrados no sistema.
     *
     * <p>
     * Este método é restrito a usuários com perfil {@code ADMIN} ou {@code OWNER}.
     * </p>
     *
     * @return lista de todos os {@link User} cadastrados
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Cria um novo usuário com perfil de administrador ({@link UserRole#ADMIN}).
     *
     * <p>
     * A senha é automaticamente codificada com BCrypt antes de ser persistida. Esta operação é
     * exclusiva para usuários com perfil {@code OWNER}.
     * </p>
     *
     * @param input DTO contendo nome, e-mail e senha do novo administrador
     * @return o usuário administrador criado e persistido
     * @throws java.util.NoSuchElementException se o papel ADMIN não existir no banco de dados
     */
    public User createAdministrator(RegisterUserRequest input) {
        if (userRepository.existsByEmail(input.email())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Role role = roleRepository.findByName(UserRole.ADMIN).orElseThrow();

        User user = new User();
        user.setName(input.name());
        user.setEmail(input.email());
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setRole(role);

        return userRepository.save(user);

    }
}
