package ru.vsu.cs.platon.docs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.cs.platon.docs.dto.*;
import ru.vsu.cs.platon.docs.mapper.DocumentMapper;
import ru.vsu.cs.platon.docs.mapper.DocumentPermissionMapper;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentRole;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.UserRepository;
import ru.vsu.cs.platon.docs.service.DocumentPermissionService;
import ru.vsu.cs.platon.docs.service.DocumentService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentPermissionService permissionService;
    private final UserRepository userRepository;

    // Список документов владельца с пагинацией
    @GetMapping("/me")
    public ResponseEntity<List<DocumentDTO>> listDocuments(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Page<Document> documents = documentService.getDocumentsByOwner(owner, PageRequest.of(page, size));
        List<DocumentDTO> documentDTOs = documents.stream()
                .map(DocumentMapper::toDocumentDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentDTOs);
    }

    // Создать документ
    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody CreateDocumentRequestDTO request) {
        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Document document = documentService.createDocument(owner, request.getTitle());
        return ResponseEntity.ok(DocumentMapper.toDocumentDTO(document));
    }

    private Document getDocumentForOwner(UserDetails userDetails, UUID documentId) {
        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Document document = documentService.findByIdAndOwner(documentId, owner)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Document not found"));

        if (!document.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("You do not have permission to perform this action.");
        }

        return document;
    }

    @PostMapping("/{documentId}/share")
    public ResponseEntity<?> shareDocument(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable UUID documentId,
                                           @RequestBody ShareDocumentRequestDTO request) {

        Document document = getDocumentForOwner(userDetails, documentId);
        permissionService.shareDocument(document, request.getEmail(), request.getRole());

        return ResponseEntity.ok("Document shared successfully");
    }

    @GetMapping("/{documentId}/permissions")
    public ResponseEntity<List<DocumentPermissionDTO>> viewPermissions(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @PathVariable UUID documentId) {

        Document document = getDocumentForOwner(userDetails, documentId);

        List<DocumentPermissionDTO> permissions = permissionService.getPermissionsForDocument(document)
                .stream()
                .map(DocumentPermissionMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentDTO> updateDocument(@AuthenticationPrincipal UserDetails userDetails,
                                                      @PathVariable UUID documentId,
                                                      @RequestBody UpdateDocumentRequestDTO request) {

        Document document = getDocumentForOwner(userDetails, documentId);
        Document updatedDocument = documentService.updateDocument(document, request.getTitle(), request.getAccessLevel());

        return ResponseEntity.ok(DocumentMapper.toDocumentDTO(updatedDocument));
    }

    @DeleteMapping("/{documentId}/permissions/{email}")
    public ResponseEntity<?> removePermission(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable UUID documentId,
                                              @PathVariable String email) {

        Document document = getDocumentForOwner(userDetails, documentId);
        permissionService.removePermission(document, email);

        return ResponseEntity.ok("Permission removed successfully");
    }
}
