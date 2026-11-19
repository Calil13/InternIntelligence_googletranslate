package org.example.googletranslate.controller;

import org.example.googletranslate.dto.TranslationRequest;
import org.example.googletranslate.dto.TranslationResponse;
import org.example.googletranslate.dto.VoiceTranslateRequest;
import org.example.googletranslate.model.Language;
import org.example.googletranslate.service.LanguageService;
import org.example.googletranslate.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TranslationController {

    private final TranslationService translationService;
    private final LanguageService languageService;

    public TranslationController(TranslationService translationService, LanguageService languageService) {
        this.translationService = translationService;
        this.languageService = languageService;
    }

    @PostMapping("/translate")
    public ResponseEntity<TranslationResponse> translate(@RequestBody TranslationRequest request) {
        TranslationResponse response = translationService.translate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<Language>> getLanguages() {
        return ResponseEntity.ok(languageService.getAllLanguages());
    }

    @PostMapping("/voice/translate")
    public ResponseEntity<TranslationResponse> voiceTranslate(@RequestBody VoiceTranslateRequest voiceRequest) {
        TranslationRequest req = new TranslationRequest(
                voiceRequest.getSourceLanguage(),
                voiceRequest.getTargetLanguage(),
                voiceRequest.getTranscript()
        );
        TranslationResponse response = translationService.translate(req);
        return ResponseEntity.ok(response);
    }
}
