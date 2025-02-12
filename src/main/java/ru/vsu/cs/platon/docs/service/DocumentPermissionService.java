package ru.vsu.cs.platon.docs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentPermission;
import ru.vsu.cs.platon.docs.model.DocumentRole;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.DocumentPermissionRepository;
import ru.vsu.cs.platon.docs.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentPermissionService {

    private final DocumentPermissionRepository permissionRepository;
    private final UserRepository userRepository;


    public Optional<DocumentPermission> hasPermission(Document document, User user){
        return permissionRepository.findByDocumentAndUser(document, user);
    }

    // Поделиться документом с другим пользователем
    public DocumentPermission shareDocument(Document document, String userEmail, DocumentRole role) {
        // Поиск пользователя по email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        // Проверка существующего разрешения
        Optional<DocumentPermission> existingPermissionOpt = hasPermission(document, user);

        if (existingPermissionOpt.isPresent()) {
            // Если разрешение уже существует, обновляем его роль
            DocumentPermission existingPermission = existingPermissionOpt.get();
            existingPermission.setRole(role);
            return permissionRepository.save(existingPermission);
        } else {
            // Если разрешения нет, создаем новое
            DocumentPermission newPermission = DocumentPermission.builder()
                    .document(document)
                    .user(user)
                    .role(role)
                    .build();
            return permissionRepository.save(newPermission);
        }
    }

    // Получить разрешения для документа
    public List<DocumentPermission> getPermissionsForDocument(Document document) {
        return permissionRepository.findByDocument(document);
    }

    // Изменить разрешение для пользователя
    public Optional<DocumentPermission> updatePermission(Document document, String userEmail, DocumentRole newRole) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        Optional<DocumentPermission> permissionOpt = permissionRepository.findByDocumentAndUser(document, user);
        if (permissionOpt.isPresent()) {
            DocumentPermission permission = permissionOpt.get();
            permission.setRole(newRole);
            permissionRepository.save(permission);
        }
        return permissionOpt;
    }

    // Удалить разрешение
    public void removePermission(Document document, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        permissionRepository.findByDocumentAndUser(document, user)
                .ifPresent(permissionRepository::delete);
    }
}
