package ru.vsu.cs.platon.docs.dto.auth;

import lombok.Data;

@Data
public class RefreshRequestDTO {
    private String refreshToken;
}
