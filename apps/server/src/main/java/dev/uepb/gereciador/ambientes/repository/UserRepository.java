package dev.uepb.gereciador.ambientes.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import dev.uepb.gereciador.ambientes.entity.User;

/**
 * Repositório MongoDB para operações de persistência da entidade {@link User}.
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Busca um usuário pelo endereço de e-mail.
     *
     * <p>
     * Utilizado pelo {@link dev.uepb.gereciador.ambientes.config.AuthConfig} para carregar os
     * detalhes do usuário durante a autenticação.
     * </p>
     *
     * @param username o endereço de e-mail do usuário
     * @return um {@link Optional} contendo os detalhes do usuário, ou vazio se não encontrado
     */
    Optional<UserDetails> findUserByEmail(String username);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     *
     * @param email o e-mail a ser verificado
     * @return true se o e-mail já estiver em uso, false caso contrário
     */
    boolean existsByEmail(String email);
}
