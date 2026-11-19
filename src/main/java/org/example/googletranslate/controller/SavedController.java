package org.example.googletranslate.controller;

import org.example.googletranslate.dto.SavedRequest;
import org.example.googletranslate.model.SavedTranslation;
import org.example.googletranslate.service.SavedService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved")
@CrossOrigin(origins = "*")
public class SavedController {

    private final SavedService savedService;

    public SavedController(SavedService savedService) {
        this.savedService = savedService;
    }

    @GetMapping
    public ResponseEntity<List<SavedTranslation>> getSaved(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String sourceLang,
            @RequestParam(required = false) String targetLang) {
        return ResponseEntity.ok(savedService.getSaved(query, sourceLang, targetLang));
    }

    @PostMapping
    public ResponseEntity<SavedTranslation> saveTranslation(@RequestBody SavedRequest req) {
        SavedTranslation saved = savedService.saveTranslation(req);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSaved(@PathVariable Long id) {
        savedService.removeSaved(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Tərcümə yaddaşdan silindi"));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearAll() {
        savedService.clearAll();
        return ResponseEntity.ok(Map.of("success", true, "message", "Bütün yadda saxlanılanlar təmizləndi"));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSaved() {
        String csv = savedService.exportToCsv();
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"saved_translations.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
