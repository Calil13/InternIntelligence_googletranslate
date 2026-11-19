package org.example.googletranslate.controller;

import org.example.googletranslate.dto.AccountStatsResponse;
import org.example.googletranslate.dto.ProfileUpdateRequest;
import org.example.googletranslate.model.UserProfile;
import org.example.googletranslate.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<AccountStatsResponse> getAccount() {
        return ResponseEntity.ok(accountService.getAccountStats());
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateProfile(@RequestBody ProfileUpdateRequest req) {
        return ResponseEntity.ok(accountService.updateProfile(req));
    }

    @PostMapping("/reset-stats")
    public ResponseEntity<Map<String, Object>> resetStats() {
        accountService.resetStats();
        return ResponseEntity.ok(Map.of("success", true, "message", "Statistika sıfırlandı"));
    }
}
