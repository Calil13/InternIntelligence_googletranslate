package org.example.googletranslate.service;

import org.example.googletranslate.dto.SettingsUpdateRequest;
import org.example.googletranslate.model.AppSettings;
import org.example.googletranslate.repository.AppSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final AppSettingsRepository settingsRepository;

    public SettingsService(AppSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public AppSettings getOrCreateSettings() {
        return settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> settingsRepository.save(AppSettings.defaultSettings()));
    }

    public AppSettings updateSettings(SettingsUpdateRequest req) {
        AppSettings settings = getOrCreateSettings();

        if (req.getTheme() != null && !req.getTheme().isBlank()) {
            settings.setTheme(req.getTheme().toLowerCase());
        }
        if (req.getAutoDetect() != null) {
            settings.setAutoDetect(req.getAutoDetect());
        }
        if (req.getInstantTranslate() != null) {
            settings.setInstantTranslate(req.getInstantTranslate());
        }
        if (req.getSpeechSpeed() != null) {
            settings.setSpeechSpeed(req.getSpeechSpeed());
        }
        if (req.getVoicePitch() != null) {
            settings.setVoicePitch(req.getVoicePitch());
        }
        if (req.getAutoPlayAudio() != null) {
            settings.setAutoPlayAudio(req.getAutoPlayAudio());
        }
        if (req.getHistoryEnabled() != null) {
            settings.setHistoryEnabled(req.getHistoryEnabled());
        }
        if (req.getInterfaceLanguage() != null && !req.getInterfaceLanguage().isBlank()) {
            settings.setInterfaceLanguage(req.getInterfaceLanguage().toLowerCase());
        }

        return settingsRepository.save(settings);
    }

    public AppSettings resetSettings() {
        AppSettings current = getOrCreateSettings();
        AppSettings defaults = AppSettings.defaultSettings();
        defaults.setId(current.getId());
        return settingsRepository.save(defaults);
    }
}
