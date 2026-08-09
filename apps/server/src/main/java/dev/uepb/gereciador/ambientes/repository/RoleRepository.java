package dev.uepb.gereciador.ambientes.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.enums.UserRole;

/**
 * Repositório MongoDB para operações de persistência da entidade {@link Role}.
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    /**
     * Busca um papel pelo seu nome (enum {@link UserRole}).
     *
     * @param name o tipo do papel desejado
     * @return um {@link Optional} contendo o papel, ou vazio se não encontrado
     */
    Optional<Role> findByName(UserRole name);
}
