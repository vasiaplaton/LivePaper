package ru.vsu.cs.platon.docs.model.clickhouse;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_snapshots")
@Data
public class DocumentSnapshot {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "document_id", nullable = false, columnDefinition = "UUID")
    private UUID documentId;

    @Column(name = "snapshot_path", nullable = false, columnDefinition = "TEXT")
    private String snapshotPath;

    @Column(name = "applied_diff_id", columnDefinition = "UUID")
    private UUID appliedDiffId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
