package ru.vsu.cs.platon.docs.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentAccessLevel;
import ru.vsu.cs.platon.docs.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    // Find all documents owned by a specific user
    List<Document> findByOwner(User owner);

    Page<Document> findByOwner(User owner, Pageable pageable);

    // Find document by title
    Optional<Document> findByTitle(String title);

    // Find documents accessible by public access level (READ_ONLY or EDITABLE)
    List<Document> findByAccessLevelIn(List<DocumentAccessLevel> accessLevels);


    @Query("SELECT dp.document FROM DocumentPermission dp " +
            "WHERE dp.user = :user")
    Page<Document> findSharedDocumentsByUser(@Param("user") User user, Pageable pageable);
}
