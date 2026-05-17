package com.tirana.events.controller;

import com.tirana.events.dto.CityGuideLocationDTO;
import com.tirana.events.service.CityGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city-guide")
@RequiredArgsConstructor
public class CityGuideController {
    private final CityGuideService cityGuideService;

    @GetMapping("/events/{eventId}/bus-stops")
    public ResponseEntity<List<CityGuideLocationDTO>> getBusStops(@PathVariable Long eventId) {
        return ResponseEntity.ok(cityGuideService.getBusStops(eventId));
    }

    @GetMapping("/events/{eventId}/restaurants")
    public ResponseEntity<List<CityGuideLocationDTO>> getRestaurants(@PathVariable Long eventId) {
        return ResponseEntity.ok(cityGuideService.getNearbyRestaurants(eventId));
    }

    @GetMapping("/events/{eventId}/parking")
    public ResponseEntity<List<CityGuideLocationDTO>> getParking(@PathVariable Long eventId) {
        return ResponseEntity.ok(cityGuideService.getParking(eventId));
    }
}
