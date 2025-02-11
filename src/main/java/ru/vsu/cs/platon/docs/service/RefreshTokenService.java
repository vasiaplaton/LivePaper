package ru.vsu.cs.platon.docs.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.platon.docs.model.RefreshToken;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenDurationDays = 7;  // Жизнь токена - 7 дней

    // Генерация токена
    public RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(refreshTokenDurationDays))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    // Поиск токена по его значению
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // Проверка токена на валидность
    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    // Удаление токена по объекту токена
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    // Удаление всех токенов пользователя (например, при выходе)
    public void deleteTokensForUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    // Обновление токена: удаление старого и создание нового
    public RefreshToken refreshToken(RefreshToken oldToken, HttpServletRequest request) {
        deleteToken(oldToken);
        return createRefreshToken(oldToken.getUser(), request);
    }
}
