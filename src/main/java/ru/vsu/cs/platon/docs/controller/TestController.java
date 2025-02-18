package ru.vsu.cs.platon.docs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.platon.docs.model.clickhouse.DocumentDiff;
import ru.vsu.cs.platon.docs.service.ClickHouseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final ClickHouseService clickHouseService;

    @GetMapping("/diffs/{documentId}")
    public ResponseEntity<List<DocumentDiff>> getAllDiffs(@PathVariable UUID documentId) {
        List<DocumentDiff> diffs = clickHouseService.getDiffs(documentId);
        return ResponseEntity.ok(diffs);
    }
}
