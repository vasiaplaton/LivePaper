package ru.vsu.cs.platon.docs.dto;

import lombok.Data;
import ru.vsu.cs.platon.docs.model.DocumentRole;

@Data
public class ShareDocumentRequestDTO {
    private String email;
    private DocumentRole role;
}
