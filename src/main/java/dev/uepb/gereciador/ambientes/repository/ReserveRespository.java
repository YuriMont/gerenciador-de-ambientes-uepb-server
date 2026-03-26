package dev.uepb.gereciador.ambientes.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import dev.uepb.gereciador.ambientes.entity.Reserve;

/**
 * Repositório MongoDB para operações de persistência da entidade {@link Reserve}.
 *
 * <p>Além das operações CRUD padrão herdadas do {@link MongoRepository}, provê
 * método de consulta derivado para busca de reservas por ambiente e data.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
public interface ReserveRespository extends MongoRepository<Reserve, String> {

    /**
     * Retorna todas as reservas de um ambiente em uma data específica.
     *
     * <p>Utilizado para verificar conflitos de horário e retornar os slots
     * disponíveis para reserva em um determinado dia.</p>
     *
     * @param environmentId o identificador do ambiente
     * @param date          a data de consulta
     * @return lista de reservas encontradas para o ambiente e data informados
     */
    List<Reserve> findAllByEnvironmentIdAndDate(String environmentId, LocalDate date);
}
