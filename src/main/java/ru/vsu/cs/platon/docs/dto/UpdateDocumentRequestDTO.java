package ru.vsu.cs.platon.docs.dto;

import lombok.Data;
import ru.vsu.cs.platon.docs.model.DocumentAccessLevel;

@Data
public class UpdateDocumentRequestDTO {
    private String title;
    private DocumentAccessLevel accessLevel;
}
