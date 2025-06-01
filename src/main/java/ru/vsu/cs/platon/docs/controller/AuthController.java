package ru.vsu.cs.platon.docs.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.platon.docs.dto.auth.RegisterRequestDTO;
import ru.vsu.cs.platon.docs.dto.auth.LoginRequestDTO;
import ru.vsu.cs.platon.docs.dto.auth.RefreshRequestDTO;
import ru.vsu.cs.platon.docs.dto.auth.AuthResponseDTO;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.model.RefreshToken;
import ru.vsu.cs.platon.docs.model.UserRole;
import ru.vsu.cs.platon.docs.repository.postgres.UserRepository;
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

    // === РЕГИСТРАЦИЯ ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User with this email already exists");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    // === ЛОГИН ===
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, httpRequest);

        return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken.getToken()));
    }

    // === ОБНОВЛЕНИЕ ТОКЕНА ===
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshRequestDTO request, HttpServletRequest httpRequest) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(request.getRefreshToken());

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

        return ResponseEntity.ok(new AuthResponseDTO(newAccessToken, newRefreshToken.getToken()));
    }
}
