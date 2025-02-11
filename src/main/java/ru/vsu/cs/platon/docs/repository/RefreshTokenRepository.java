package ru.vsu.cs.platon.docs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);  // Найти токен по значению
    void deleteByUserId(Long userId);  // Удалить все токены для пользователя
}
