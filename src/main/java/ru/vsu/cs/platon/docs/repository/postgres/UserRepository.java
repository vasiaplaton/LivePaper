package ru.vsu.cs.platon.docs.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
