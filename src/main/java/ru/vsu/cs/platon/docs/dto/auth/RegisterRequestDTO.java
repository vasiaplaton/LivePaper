package ru.vsu.cs.platon.docs.dto.auth;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String username;
}
