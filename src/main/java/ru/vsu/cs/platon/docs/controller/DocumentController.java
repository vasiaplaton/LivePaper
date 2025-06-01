package ru.vsu.cs.platon.docs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vsu.cs.platon.docs.dto.*;
import ru.vsu.cs.platon.docs.mapper.DocumentMapper;
import ru.vsu.cs.platon.docs.mapper.DocumentPermissionMapper;
import ru.vsu.cs.platon.docs.model.Document;
import ru.vsu.cs.platon.docs.model.DocumentAccessLevel;
import ru.vsu.cs.platon.docs.model.DocumentPermission;
import ru.vsu.cs.platon.docs.model.User;
import ru.vsu.cs.platon.docs.repository.postgres.DocumentRepository;
import ru.vsu.cs.platon.docs.repository.postgres.UserRepository;
import ru.vsu.cs.platon.docs.service.DocumentPermissionService;
import ru.vsu.cs.platon.docs.service.DocumentService;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentPermissionService permissionService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;


    protected User findUser(UserDetails userDetails){
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
    }

    public enum DocumentSortBy{
        TITLE("title"),
        CREATED_AT("createdAt"),
        UPDATED_AT("updatedAt");

        private final String field;

        DocumentSortBy(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

    // Список документов владельца с пагинацией
    @GetMapping("/me")
    public ResponseEntity<List<DocumentDTO>> listDocuments(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "CREATED_AT") DocumentSortBy sortBy,
                                                           @RequestParam(defaultValue = "asc") String order) {
        User owner = findUser(userDetails);

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy.getField()));

        Page<Document> documents = documentService.getDocumentsByOwner(owner, pageable);
        List<DocumentDTO> documentDTOs = documents.stream()
                .map(DocumentMapper::toDocumentDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentDTOs);
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<DocumentDTO>> getSharedDocuments(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "CREATED_AT") DocumentSortBy sortBy,
                                                                @RequestParam(defaultValue = "asc") String order) {
        User user = findUser(userDetails);

        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy.getField()));

        Page<Document> sharedDocuments = documentService.getDocumentsSharedWithUser(user, pageable);

        List<DocumentDTO> documentDTOs = sharedDocuments.stream()
                .map(DocumentMapper::toDocumentDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(documentDTOs);
    }

    // Создать документ
    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody CreateDocumentRequestDTO request) {
        User owner = findUser(userDetails);

        Document document = documentService.createDocument(owner, request.getTitle());
        return ResponseEntity.ok(DocumentMapper.toDocumentDTO(document));
    }

    private Document getDocumentForOwner(UserDetails userDetails, UUID documentId) {
        User owner = findUser(userDetails);

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


    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDTO> getDocumentById(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable UUID documentId) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Document document = documentService.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Document not found"));

        if (document.getAccessLevel() != DocumentAccessLevel.PRIVATE){
            return ResponseEntity.ok(DocumentMapper.toDocumentDTO(document));
        }

        Optional<DocumentPermission> documentPermission = permissionService.hasPermission(document, currentUser);

        if (documentPermission.isEmpty()) {
            throw new ResponseStatusException(FORBIDDEN, "You do not have permission to access this document.");
        }

        return ResponseEntity.ok(DocumentMapper.toDocumentDTO(document));
    }
}
