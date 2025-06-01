package ru.vsu.cs.platon.docs.repository.clickhouse;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentDiff;

import java.util.List;
import java.util.UUID;

public interface DocumentDiffRepository extends JpaRepository<DocumentDiff, UUID> {

    /**
     * Find all diffs for a given document, ordered by creation time descending.
     */
    List<DocumentDiff> findAllByDocumentIdOrderByCreatedAtDesc(UUID documentId);
}
