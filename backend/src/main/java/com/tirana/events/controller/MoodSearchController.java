package com.tirana.events.controller;

import com.tirana.events.dto.MoodSearchRequest;
import com.tirana.events.dto.MoodSearchResultDTO;
import com.tirana.events.service.MoodSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mood-search")
@RequiredArgsConstructor
public class MoodSearchController {
    private final MoodSearchService moodSearchService;

    @PostMapping
    public ResponseEntity<MoodSearchResultDTO> searchByMood(
            @RequestBody MoodSearchRequest request,
            Authentication auth) {
        Long userId = auth != null ? Long.parseLong(auth.getName()) : null;
        return ResponseEntity.ok(moodSearchService.searchByMood(userId, request));
    }
}
