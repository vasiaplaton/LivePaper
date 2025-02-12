package ru.vsu.cs.platon.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.platon.docs.model.DocumentRole;

@Data
@AllArgsConstructor
public class DocumentPermissionDTO {
    private Long id;
    private Long userId;
    private String username;
    private DocumentRole role;
}
