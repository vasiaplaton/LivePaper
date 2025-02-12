package ru.vsu.cs.platon.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.platon.docs.model.UserRole;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private UserRole role;
    private List<DocumentDTO> documents;
}
