package com.tirana.events.controller;

import com.tirana.events.dto.EventDTO;
import com.tirana.events.dto.OnboardingRequest;
import com.tirana.events.dto.TrackInteractionRequest;
import com.tirana.events.model.*;
import com.tirana.events.repository.CategoryRepository;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.service.PersonalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personalization")
@RequiredArgsConstructor
public class PersonalizationController {
    
    private final PersonalizationService personalizationService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    
    @GetMapping("/feed")
    public ResponseEntity<List<EventDTO>> getPersonalizedFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "20") int limit) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Event> events = personalizationService.getPersonalizedFeed(user, limit);
        List<EventDTO> eventDTOs = events.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(eventDTOs);
    }
    
    @PostMapping("/track")
    public ResponseEntity<Void> trackInteraction(
            Authentication authentication,
            @RequestBody TrackInteractionRequest request) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(request.getEventId())
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        personalizationService.trackInteraction(user, event, request.getType());
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/onboarding/status")
    public ResponseEntity<Boolean> checkOnboardingStatus(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean needsOnboarding = personalizationService.needsOnboarding(user);
        return ResponseEntity.ok(needsOnboarding);
    }
    
    @PostMapping("/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding(
            Authentication authentication,
            @RequestBody OnboardingRequest request) {
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Set<Category> categories = request.getCategoryIds().stream()
            .map(id -> categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id)))
            .collect(Collectors.toSet());
        
        personalizationService.completeOnboarding(user, categories);
        
        return ResponseEntity.ok().build();
    }
    
    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setLatitude(event.getLatitude());
        dto.setLongitude(event.getLongitude());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setImageUrl(event.getImageUrl());
        dto.setMaxAttendees(event.getMaxAttendees());
        dto.setCreatedAt(event.getCreatedAt());
        
        if (event.getCategory() != null) {
            dto.setCategoryId(event.getCategory().getId());
            dto.setCategoryName(event.getCategory().getName());
        }
        
        if (event.getOrganizer() != null) {
            dto.setOrganizerId(event.getOrganizer().getId());
            dto.setOrganizerName(event.getOrganizer().getFullName());
        }
        
        return dto;
    }
}
