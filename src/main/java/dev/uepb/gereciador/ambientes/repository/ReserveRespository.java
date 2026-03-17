package dev.uepb.gereciador.ambientes.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import dev.uepb.gereciador.ambientes.entity.Reserve;

public interface ReserveRespository extends MongoRepository<Reserve, String> {
    List<Reserve> findAllByEnvironmentIdAndDate(String environmentId, LocalDate date);
}
