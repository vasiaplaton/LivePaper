package ru.vsu.cs.platon.docs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentAccessLevel;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.postgres.DocumentRepository;
import ru.vsu.cs.platon.docs.service.file.FileService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileService fileService;

    // Получить все документы владельца с пагинацией
    public Page<Document> getDocumentsByOwner(User owner, Pageable pageable) {
        return documentRepository.findByOwner(owner, pageable);
    }

    // Создать документ
    public Document createDocument(User owner, String title) {
        byte[] emptyFileContent = "".getBytes();  // Пустой файл для начала
        String filePath = fileService.saveFile(emptyFileContent);
        System.out.println("filePath: " + filePath);
        Document document = Document.builder()
                .title(title)
                .owner(owner)
                .filePath(filePath)
                .accessLevel(DocumentAccessLevel.PRIVATE)
                .build();
        return documentRepository.save(document);
    }

    // Найти документ по ID и владельцу
    public Optional<Document> findByIdAndOwner(UUID documentId, User owner) {
        return documentRepository.findById(documentId)
                .filter(doc -> doc.getOwner().equals(owner));
    }

    public Optional<Document> findById(UUID documentId) {
        return documentRepository.findById(documentId);
    }

    // Обновить документ
    public Document updateDocument(Document document, String title, DocumentAccessLevel accessLevel) {
        document.setTitle(title);
        document.setAccessLevel(accessLevel);
        return documentRepository.save(document);
    }

    public Page<Document> getDocumentsSharedWithUser(User user, Pageable pageable) {
        return documentRepository.findSharedDocumentsByUser(user, pageable);
    }

}
