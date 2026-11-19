package org.example.googletranslate.controller;

import org.example.googletranslate.dto.SettingsUpdateRequest;
import org.example.googletranslate.model.AppSettings;
import org.example.googletranslate.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<AppSettings> getSettings() {
        return ResponseEntity.ok(settingsService.getOrCreateSettings());
    }

    @PutMapping
    public ResponseEntity<AppSettings> updateSettings(@RequestBody SettingsUpdateRequest req) {
        return ResponseEntity.ok(settingsService.updateSettings(req));
    }

    @PostMapping("/reset")
    public ResponseEntity<AppSettings> resetSettings() {
        return ResponseEntity.ok(settingsService.resetSettings());
    }
}
