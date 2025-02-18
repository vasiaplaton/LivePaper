package ru.vsu.cs.platon.docs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentDiff;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentSnapshot;
import ru.vsu.cs.platon.docs.repository.DocumentDiffRepository;
import ru.vsu.cs.platon.docs.repository.DocumentSnapshotRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClickHouseService {

    private final DocumentDiffRepository documentDiffRepository;
    private final DocumentSnapshotRepository documentSnapshotRepository;

    public void saveDiff(UUID documentId, String diffContent, UUID previousDiffId) {
        DocumentDiff diff = new DocumentDiff();
        diff.setId(UUID.randomUUID());
        diff.setDocumentId(documentId);
        diff.setDiffContent(diffContent);
        diff.setPreviousDiffId(previousDiffId);
        diff.setDiffProcessed(false);
        diff.setCreatedAt(LocalDateTime.now());

        documentDiffRepository.save(diff);
    }

    public List<DocumentDiff> getDiffs(UUID documentId) {
        return documentDiffRepository.findByDocumentId(documentId);
    }

    public Optional<DocumentSnapshot> getLatestSnapshot(UUID documentId) {
        return documentSnapshotRepository.findLatestSnapshotByDocumentId(documentId);
    }

    public void saveSnapshot(UUID documentId, String snapshotPath, UUID appliedDiffId) {
        DocumentSnapshot snapshot = new DocumentSnapshot();
        snapshot.setId(UUID.randomUUID());
        snapshot.setDocumentId(documentId);
        snapshot.setSnapshotPath(snapshotPath);
        snapshot.setAppliedDiffId(appliedDiffId);
        snapshot.setCreatedAt(LocalDateTime.now());

        documentSnapshotRepository.save(snapshot);
    }
}
