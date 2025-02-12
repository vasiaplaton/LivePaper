package ru.vsu.cs.platon.docs.mapper;

import ru.vsu.cs.platon.docs.dto.DocumentDTO;
import ru.vsu.cs.platon.docs.model.Document;

public class DocumentMapper {

    public static DocumentDTO toDocumentDTO(Document document) {
        return new DocumentDTO(
                document.getId(),
                document.getTitle(),
                document.getCreatedAt().toString(),
                document.getUpdatedAt().toString(),
                document.getOwner().getEmail(),
                document.getAccessLevel()
        );
    }
}
