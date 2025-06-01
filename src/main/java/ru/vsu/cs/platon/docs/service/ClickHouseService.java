package ru.vsu.cs.platon.docs.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentDiff;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentSnapshot;
import ru.vsu.cs.platon.docs.repository.clickhouse.DocumentDiffRepository;
import ru.vsu.cs.platon.docs.repository.clickhouse.DocumentSnapshotRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClickHouseService {

    private final DocumentDiffRepository documentDiffRepository;
    private final DocumentSnapshotRepository documentSnapshotRepository;

    /**
     * Сохранить новый diff.
     */
    public void saveDiff(UUID documentId, String diffContent, UUID previousDiffId) {
        DocumentDiff diff = new DocumentDiff();
        diff.setId(UUID.randomUUID());
        diff.setDocumentId(documentId);
        diff.setDiffContent(diffContent);
        diff.setPreviousDiffId(previousDiffId);
        diff.setDiffProcessed(false);
        diff.setCreatedAt(LocalDateTime.now());

        // здесь можно вызвать insertDiff, когда он раскомментирован:
        // documentDiffRepository.insertDiff(diff);
    }

    /**
     * Получить все diffs документа (пока заглушка).
     */
    public List<DocumentDiff> getDiffs(UUID documentId) {
        // Если появится метод repository.findByDocumentId(...), можно заменить:
        return documentDiffRepository.findAllByDocumentIdOrderByCreatedAtDesc(documentId);
    }

    /**
     * Последний снапшот по документу.
     */
    public Optional<DocumentSnapshot> getLatestSnapshot(UUID documentId) {
        return documentSnapshotRepository.findLatestByDocumentId(documentId);
    }

    /**
     * Сохранить новый снапшот.
     */
    public void saveSnapshot(UUID documentId, String snapshotPath, UUID appliedDiffId) {
        DocumentSnapshot snapshot = new DocumentSnapshot();
        snapshot.setId(UUID.randomUUID());
        snapshot.setDocumentId(documentId);
        snapshot.setSnapshotPath(snapshotPath);
        snapshot.setAppliedDiffId(appliedDiffId);
        snapshot.setCreatedAt(LocalDateTime.now());

        // TODO
        // documentSnapshotRepository.save(snapshot);
    }
}
