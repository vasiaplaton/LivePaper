package ru.vsu.cs.platon.docs.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentSnapshot;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentSnapshotRepository extends CrudRepository<DocumentSnapshot, UUID> {

    @Query("SELECT * FROM document_snapshots WHERE document_id = :documentId ORDER BY created_at DESC LIMIT 1")
    Optional<DocumentSnapshot> findLatestSnapshotByDocumentId(UUID documentId);
}
