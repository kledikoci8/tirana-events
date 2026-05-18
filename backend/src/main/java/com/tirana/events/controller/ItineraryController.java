package com.tirana.events.controller;

import com.tirana.events.dto.*;
import com.tirana.events.service.ItineraryService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
public class ItineraryController {
    private final CurrentUserService currentUserService;
    private final ItineraryService itineraryService;

    @PostMapping
    public ResponseEntity<SavedItineraryDTO> createItinerary(
            @RequestBody CreateItineraryRequest request,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(itineraryService.createItinerary(userId, request));
    }

    @GetMapping("/my-itineraries")
    public ResponseEntity<List<SavedItineraryDTO>> getMyItineraries(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(itineraryService.getUserItineraries(userId));
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<SavedItineraryDTO> getSharedItinerary(@PathVariable String token) {
        return ResponseEntity.ok(itineraryService.getItineraryByShareToken(token));
    }

    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<Void> deleteItinerary(
            @PathVariable Long itineraryId,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        itineraryService.deleteItinerary(itineraryId, userId);
        return ResponseEntity.ok().build();
    }
}
