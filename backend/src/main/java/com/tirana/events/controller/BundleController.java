package com.tirana.events.controller;

import com.tirana.events.dto.EventBundleDTO;
import com.tirana.events.service.EventBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundles")
@RequiredArgsConstructor
public class BundleController {
    private final EventBundleService bundleService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<EventBundleDTO>> getEventBundles(@PathVariable Long eventId) {
        return ResponseEntity.ok(bundleService.getEventBundles(eventId));
    }

    @GetMapping("/events/{eventId}/restaurants")
    public ResponseEntity<List<EventBundleDTO>> getRestaurants(@PathVariable Long eventId) {
        return ResponseEntity.ok(bundleService.getRestaurantRecommendations(eventId));
    }

    @GetMapping("/events/{eventId}/transport")
    public ResponseEntity<List<EventBundleDTO>> getTransport(@PathVariable Long eventId) {
        return ResponseEntity.ok(bundleService.getTransportOptions(eventId));
    }

    @GetMapping("/events/{eventId}/alternatives")
    public ResponseEntity<List<EventBundleDTO>> getAlternatives(@PathVariable Long eventId) {
        return ResponseEntity.ok(bundleService.getWeatherAlternatives(eventId));
    }
}
