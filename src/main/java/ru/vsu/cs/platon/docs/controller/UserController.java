package ru.vsu.cs.platon.docs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.platon.docs.dto.UserDTO;
import ru.vsu.cs.platon.docs.mapper.UserMapper;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.postgres.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Операции для управления пользователями")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает данные текущего авторизованного пользователя")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        return userOptional
                .map(user -> ResponseEntity.ok(UserMapper.toUserDTOWithDocuments(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей (только для ADMIN)")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его идентификатору (только для ADMIN)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toUserDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
