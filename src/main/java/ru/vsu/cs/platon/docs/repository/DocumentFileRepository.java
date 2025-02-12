package ru.vsu.cs.platon.docs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentFile;

import java.util.List;

public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long> {

    // Find all files attached to a specific document
    List<DocumentFile> findByDocument(Document document);

    // Find encrypted files
    List<DocumentFile> findByEncryptedTrue();
}
