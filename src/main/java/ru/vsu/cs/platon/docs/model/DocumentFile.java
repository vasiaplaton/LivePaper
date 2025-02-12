package ru.vsu.cs.platon.docs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private String filePath; // Path to the file (image, attachment)

    @Column(nullable = false)
    private boolean encrypted = true; // Whether the file is encrypted

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
