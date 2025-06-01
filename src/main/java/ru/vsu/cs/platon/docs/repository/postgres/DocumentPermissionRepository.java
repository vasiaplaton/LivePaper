package ru.vsu.cs.platon.docs.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentPermission;
import ru.vsu.cs.platon.docs.model.User;

import java.util.List;
import java.util.Optional;

public interface DocumentPermissionRepository extends JpaRepository<DocumentPermission, Long> {

    // Find permissions for a specific document
    List<DocumentPermission> findByDocument(Document document);

    // Find permissions for a specific user
    List<DocumentPermission> findByUser(User user);

    // Find a specific permission by document and user
    Optional<DocumentPermission> findByDocumentAndUser(Document document, User user);
}
