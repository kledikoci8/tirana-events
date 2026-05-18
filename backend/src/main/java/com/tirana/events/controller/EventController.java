package com.tirana.events.controller;

import com.tirana.events.dto.CreateEventRequest;
import com.tirana.events.dto.EventDTO;
import com.tirana.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// FIX B1: Removed @CrossOrigin - CORS is handled globally in SecurityConfig
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.ok(eventService.createEvent(request, authentication.getName()));
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(eventService.getUpcomingEvents(email, page, size));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<EventDTO>> getRecommendedEvents(
            Authentication authentication,
            @RequestParam(defaultValue = "20") int limit) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(eventService.getRecommendedEvents(email, limit));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<EventDTO>> getNearbyEvents(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10") Double radiusKm,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(eventService.getNearbyEvents(lat, lng, radiusKm, email));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(eventService.searchEvents(query, email, page, size));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<EventDTO>> getMyEvents(Authentication authentication) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(authentication.getName()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id,
                                                  Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(eventService.getEventById(id, email));
    }
    
    @PostMapping("/{id}/save")
    public ResponseEntity<Void> saveEvent(@PathVariable Long id,
                                          Authentication authentication) {
        eventService.saveEvent(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsaveEvent(@PathVariable Long id,
                                            Authentication authentication) {
        eventService.unsaveEvent(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
