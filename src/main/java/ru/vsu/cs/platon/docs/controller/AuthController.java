package ru.vsu.cs.platon.docs.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.model.RefreshToken;
import ru.vsu.cs.platon.docs.model.UserRole;
import ru.vsu.cs.platon.docs.repository.UserRepository;
import ru.vsu.cs.platon.docs.service.JwtService;
import ru.vsu.cs.platon.docs.service.RefreshTokenService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // === DTO КЛАССЫ ===
    public static class RegisterRequest {
        public String email;
        public String password;
        public String username;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class RefreshRequest {
        public String refreshToken;
    }

    public static class AuthResponse {
        public String accessToken;
        public String refreshToken;

        public AuthResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    // === РЕГИСТРАЦИЯ ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.badRequest().body("User with this email already exists");
        }

        User newUser = User.builder()
                .email(request.email)
                .username(request.username)
                .passwordHash(passwordEncoder.encode(request.password))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    // === ЛОГИН ===
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password, userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, httpRequest);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
    }

    // === ОБНОВЛЕНИЕ ТОКЕНА ===
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request, HttpServletRequest httpRequest) {

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(request.refreshToken);

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        if (refreshTokenService.isTokenExpired(refreshToken)) {
            refreshTokenService.deleteToken(refreshToken);
            return ResponseEntity.status(401).build();
        }

        refreshTokenService.deleteToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(refreshToken.getUser(), httpRequest);
        String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser().getEmail());

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken.getToken()));
    }
}
