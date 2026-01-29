package dev.uepb.gereciador.ambientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.uepb.gereciador.ambientes.entity.Environment;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {}
