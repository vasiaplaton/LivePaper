package ru.vsu.cs.platon.docs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentAccessLevel;
import ru.vsu.cs.platon.docs.model.User;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Find all documents owned by a specific user
    List<Document> findByOwner(User owner);

    // Find document by title
    Optional<Document> findByTitle(String title);

    // Find documents accessible by public access level (READ_ONLY or EDITABLE)
    List<Document> findByAccessLevelIn(List<DocumentAccessLevel> accessLevels);
}
