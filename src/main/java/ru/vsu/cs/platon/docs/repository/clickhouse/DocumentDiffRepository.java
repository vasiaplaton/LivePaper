package ru.vsu.cs.platon.docs.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentDiff;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentDiffRepository extends CrudRepository<DocumentDiff, UUID> {

    @Query("SELECT * FROM document_diffs WHERE document_id = :documentId ORDER BY created_at ASC")
    List<DocumentDiff> findByDocumentId(UUID documentId);
}
