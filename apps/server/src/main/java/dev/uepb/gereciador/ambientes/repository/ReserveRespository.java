package dev.uepb.gereciador.ambientes.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;

/**
 * Repositório MongoDB para operações de persistência da entidade {@link Reserve}.
 *
 * <p>Além das operações CRUD padrão herdadas do {@link MongoRepository}, provê
 * métodos de consulta derivados para as listagens da aplicação: agenda de um ambiente,
 * reservas de uma pessoa e fila de aprovação.</p>
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

    /**
     * Retorna as reservas de um ambiente em uma data que estejam em um dos status informados.
     *
     * @param environmentId o identificador do ambiente
     * @param date          a data de consulta
     * @param statuses      os status aceitos
     * @return lista de reservas encontradas
     */
    List<Reserve> findAllByEnvironmentIdAndDateAndStatusIn(String environmentId, LocalDate date,
            List<ReserveStatus> statuses);

    /**
     * Retorna as reservas de todos os ambientes em uma data que estejam em um dos status informados.
     *
     * <p>Usado para montar a agenda do dia de todos os ambientes em uma única consulta.</p>
     *
     * @param date     a data de consulta
     * @param statuses os status aceitos
     * @return lista de reservas encontradas
     */
    List<Reserve> findAllByDateAndStatusIn(LocalDate date, List<ReserveStatus> statuses);

    /**
     * Retorna as reservas de um usuário, das mais recentes para as mais antigas.
     *
     * @param userId o identificador do solicitante
     * @return lista de reservas do usuário
     */
    List<Reserve> findAllByUserIdOrderByDateDesc(String userId);

    /**
     * Retorna as reservas em um determinado status, das mais antigas para as mais recentes.
     *
     * <p>A ordem crescente por data de criação é a esperada pela fila de aprovação
     * ("Mais antigas primeiro").</p>
     *
     * @param status o status desejado
     * @return lista de reservas no status informado
     */
    List<Reserve> findAllByStatusOrderByCreatedAtAsc(ReserveStatus status);

    /**
     * Retorna as reservas cuja data esteja no intervalo informado (inclusive nas pontas).
     *
     * @param start data inicial
     * @param end   data final
     * @return lista de reservas no intervalo
     */
    List<Reserve> findAllByDateBetween(LocalDate start, LocalDate end);

    /**
     * Conta quantas reservas um usuário já solicitou.
     *
     * @param userId o identificador do solicitante
     * @return o total de reservas do usuário
     */
    long countByUserId(String userId);

    /**
     * Conta quantas reservas estão em um determinado status.
     *
     * @param status o status desejado
     * @return o total de reservas no status informado
     */
    long countByStatus(ReserveStatus status);
}
