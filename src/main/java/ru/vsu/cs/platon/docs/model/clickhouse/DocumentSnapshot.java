package ru.vsu.cs.platon.docs.model.clickhouse;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("document_snapshots")
public class DocumentSnapshot {

    @Id
    private UUID id;
    private UUID documentId;
    private String snapshotPath; // Путь к файлу со снепшотом
    private UUID appliedDiffId; // Какой diff был последним примененным к этой версии
    private LocalDateTime createdAt;
}
