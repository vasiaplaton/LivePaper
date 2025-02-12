package ru.vsu.cs.platon.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DocumentDTO {
    private UUID id;
    private String title;
    private String createdAt;
}
