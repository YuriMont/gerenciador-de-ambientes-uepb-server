package dev.uepb.gereciador.ambientes.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.uepb.gereciador.ambientes.entity.Environment;

@Repository
public interface EnvironmentRepository extends MongoRepository<Environment, String> {
}
