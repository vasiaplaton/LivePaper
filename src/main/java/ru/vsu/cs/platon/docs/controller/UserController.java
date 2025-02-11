package ru.vsu.cs.platon.docs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Операции для управления пользователями")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает данные текущего авторизованного пользователя")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        logger.info(userDetails.getUsername());
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей (только для ADMIN)")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его идентификатору (только для ADMIN)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
