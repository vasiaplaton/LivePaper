package ru.vsu.cs.platon.docs.mapper;

import ru.vsu.cs.platon.docs.dto.DocumentDTO;
import ru.vsu.cs.platon.docs.dto.UserDTO;
import ru.vsu.cs.platon.docs.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                null // Documents are not included by default
        );
    }

    public static UserDTO toUserDTOWithDocuments(User user) {
        List<DocumentDTO> documents = user.getDocuments()
                .stream()
                .limit(10)
                .map(DocumentMapper::toDocumentDTO)
                .collect(Collectors.toList());

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                documents
        );
    }
}