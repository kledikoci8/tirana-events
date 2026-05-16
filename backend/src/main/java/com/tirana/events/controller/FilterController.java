package com.tirana.events.controller;

import com.tirana.events.dto.EventDTO;
import com.tirana.events.dto.EventFilterRequest;
import com.tirana.events.dto.FilterPresetDTO;
import com.tirana.events.dto.SavePresetRequest;
import com.tirana.events.model.Event;
import com.tirana.events.model.FilterPreset;
import com.tirana.events.model.User;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
public class FilterController {
    
    private final FilterService filterService;
    private final UserRepository userRepository;
    
    @PostMapping("/events")
    public ResponseEntity<List<EventDTO>> filterEvents(@RequestBody EventFilterRequest filter) {
        List<Event> events = filterService.filterEvents(filter);
        List<EventDTO> eventDTOs = events.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(eventDTOs);
    }
    
    @PostMapping("/presets")
    public ResponseEntity<FilterPresetDTO> savePreset(
            Authentication authentication,
            @RequestBody SavePresetRequest request) {
        
        User user = getCurrentUser(authentication);
        FilterPreset preset = filterService.savePreset(user, request.getName(), request.getFilter());
        
        return ResponseEntity.ok(convertPresetToDTO(preset));
    }
    
    @GetMapping("/presets")
    public ResponseEntity<List<FilterPresetDTO>> getUserPresets(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<FilterPreset> presets = filterService.getUserPresets(user);
        
        List<FilterPresetDTO> presetDTOs = presets.stream()
            .map(this::convertPresetToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(presetDTOs);
    }
    
    @DeleteMapping("/presets/{id}")
    public ResponseEntity<Void> deletePreset(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        filterService.deletePreset(id, user);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/presets/{id}/apply")
    public ResponseEntity<List<EventDTO>> applyPreset(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        List<FilterPreset> presets = filterService.getUserPresets(user);
        
        FilterPreset preset = presets.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Preset not found"));
        
        EventFilterRequest filter = filterService.presetToFilter(preset);
        List<Event> events = filterService.filterEvents(filter);
        
        List<EventDTO> eventDTOs = events.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(eventDTOs);
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
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
    
    private FilterPresetDTO convertPresetToDTO(FilterPreset preset) {
        FilterPresetDTO dto = new FilterPresetDTO();
        dto.setId(preset.getId());
        dto.setName(preset.getName());
        dto.setMinPrice(preset.getMinPrice());
        dto.setMaxPrice(preset.getMaxPrice());
        dto.setIncludeFree(preset.getIncludeFree());
        dto.setMaxDistance(preset.getMaxDistance());
        dto.setStartHour(preset.getStartHour());
        dto.setEndHour(preset.getEndHour());
        dto.setDateRangeType(preset.getDateRangeType());
        dto.setRequireWheelchairAccess(preset.getRequireWheelchairAccess());
        dto.setRequireHearingLoop(preset.getRequireHearingLoop());
        dto.setRequireSeatedVenue(preset.getRequireSeatedVenue());
        dto.setIndoorOnly(preset.getIndoorOnly());
        dto.setOutdoorOnly(preset.getOutdoorOnly());
        dto.setCategoryIds(preset.getCategoryIds());
        dto.setCreatedAt(preset.getCreatedAt());
        return dto;
    }
}
