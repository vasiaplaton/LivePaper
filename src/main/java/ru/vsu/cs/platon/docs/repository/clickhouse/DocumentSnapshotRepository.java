package ru.vsu.cs.platon.docs.repository.clickhouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentSnapshot;

import java.util.Optional;
import java.util.UUID;

public interface DocumentSnapshotRepository extends JpaRepository<DocumentSnapshot, UUID> {

    /**
     * Finds the latest snapshot for a given document.
     */
    @Query("SELECT ds FROM ru.vsu.cs.platon.docs.model.clickhouse.DocumentSnapshot ds " +
            "WHERE ds.documentId = :documentId " +
            "ORDER BY ds.createdAt DESC")
    Optional<DocumentSnapshot> findLatestByDocumentId(@Param("documentId") UUID documentId);
}
