package dev.uepb.gereciador.ambientes.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.enums.UserRole;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(UserRole name);
}
