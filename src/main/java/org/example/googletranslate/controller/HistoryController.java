package org.example.googletranslate.controller;

import org.example.googletranslate.model.TranslationHistory;
import org.example.googletranslate.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<TranslationHistory>> getHistory(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(historyService.getHistory(query));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Long id) {
        historyService.deleteHistoryItem(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Tarixçədən silindi"));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearHistory() {
        historyService.clearHistory();
        return ResponseEntity.ok(Map.of("success", true, "message", "Tarixçə tamamilə təmizləndi"));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Map<String, Object>> toggleSave(@PathVariable Long id) {
        boolean savedState = historyService.toggleSave(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "isSaved", savedState,
                "message", savedState ? "Yadda saxlanılanlara əlavə edildi" : "Yadda saxlanılanlardan çıxarıldı"
        ));
    }
}
