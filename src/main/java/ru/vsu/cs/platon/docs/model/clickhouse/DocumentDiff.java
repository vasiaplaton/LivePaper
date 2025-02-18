package ru.vsu.cs.platon.docs.model.clickhouse;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("document_diffs")
public class DocumentDiff {

    @Id
    private UUID id;
    private UUID documentId;
    private String diffContent;  // Содержимое изменений (например, JSON)
    private UUID previousDiffId; // Ссылка на предыдущий diff
    private boolean diffProcessed; // Применен ли OT
    private LocalDateTime createdAt;
}
