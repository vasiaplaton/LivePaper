package ru.vsu.cs.platon.docs.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.io.Decoders;
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
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key, SignatureAlgorithm.HS256)
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
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
