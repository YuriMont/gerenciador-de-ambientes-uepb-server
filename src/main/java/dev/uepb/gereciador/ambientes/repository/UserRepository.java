package dev.uepb.gereciador.ambientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import dev.uepb.gereciador.ambientes.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserDetails> findUserByEmail(String username);
}
