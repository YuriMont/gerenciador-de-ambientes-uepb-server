package dev.uepb.gereciador.ambientes.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import dev.uepb.gereciador.ambientes.entity.Environment;

/**
 * Repositório MongoDB para operações de persistência da entidade {@link Environment}.
 *
 * <p>Herda operações CRUD completas do {@link MongoRepository}, incluindo
 * busca por ID, listagem, salvamento e exclusão.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Repository
public interface EnvironmentRepository extends MongoRepository<Environment, String> {
}
