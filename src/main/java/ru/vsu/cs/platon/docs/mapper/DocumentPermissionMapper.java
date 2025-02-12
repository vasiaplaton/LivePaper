package ru.vsu.cs.platon.docs.mapper;

import ru.vsu.cs.platon.docs.dto.DocumentPermissionDTO;
import ru.vsu.cs.platon.docs.model.DocumentPermission;

public class DocumentPermissionMapper {
    public static DocumentPermissionDTO toDTO(DocumentPermission permission) {
        return new DocumentPermissionDTO(
                permission.getId(),
                permission.getUser().getId(),
                permission.getUser().getUsername(),
                permission.getRole()
        );
    }
}
