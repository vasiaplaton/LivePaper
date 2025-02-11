package ru.vsu.cs.platon.docs.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Генерация Access токена
    public String generateAccessToken(String email) {
        // Access Token живёт 15 минут
        long accessTokenValidity = 1000 * 60 * 15;
        return createToken(email, accessTokenValidity);
    }

    // Создание JWT токена
    private String createToken(String subject, long validity) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // Проверка валидности токена
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Проверка на истечение срока действия токена
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Извлечение срока действия токена
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Извлечение Email (subject) из токена
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Универсальный метод извлечения данных из токена
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Извлечение всех данных из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
