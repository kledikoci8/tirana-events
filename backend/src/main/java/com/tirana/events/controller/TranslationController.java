package com.tirana.events.controller;

import com.tirana.events.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService translationService;

    @GetMapping
    public ResponseEntity<String> getTranslation(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam String fieldName,
            @RequestParam String language) {
        String translation = translationService.getTranslation(entityType, entityId, fieldName, language);
        return ResponseEntity.ok(translation);
    }

    @PostMapping
    public ResponseEntity<Void> saveTranslation(@RequestBody Map<String, Object> body) {
        translationService.saveTranslation(
            body.get("entityType").toString(),
            Long.parseLong(body.get("entityId").toString()),
            body.get("fieldName").toString(),
            body.get("language").toString(),
            body.get("translatedText").toString()
        );
        return ResponseEntity.ok().build();
    }
}
