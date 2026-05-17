package com.tirana.events.service;

import com.tirana.events.dto.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryService {
    private final SavedItineraryRepository itineraryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public SavedItineraryDTO createItinerary(Long userId, CreateItineraryRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        SavedItinerary itinerary = new SavedItinerary();
        itinerary.setUser(user);
        itinerary.setName(request.getName());
        itinerary.setDescription(request.getDescription());
        
        List<Event> events = eventRepository.findAllById(request.getEventIds());
        itinerary.setEvents(events);
        
        itinerary.setShareToken(UUID.randomUUID().toString());
        itinerary.setCreatedAt(LocalDateTime.now());
        itinerary.setUpdatedAt(LocalDateTime.now());
        
        itinerary = itineraryRepository.save(itinerary);
        
        return convertToDTO(itinerary);
    }

    public List<SavedItineraryDTO> getUserItineraries(Long userId) {
        return itineraryRepository.findByUserIdOrderByUpdatedAtDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public SavedItineraryDTO getItineraryByShareToken(String token) {
        SavedItinerary itinerary = itineraryRepository.findByShareToken(token)
            .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        return convertToDTO(itinerary);
    }

    @Transactional
    public void deleteItinerary(Long itineraryId, Long userId) {
        SavedItinerary itinerary = itineraryRepository.findById(itineraryId)
            .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        
        if (!itinerary.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        itineraryRepository.delete(itinerary);
    }

    private SavedItineraryDTO convertToDTO(SavedItinerary itinerary) {
        SavedItineraryDTO dto = new SavedItineraryDTO();
        dto.setId(itinerary.getId());
        dto.setName(itinerary.getName());
        dto.setDescription(itinerary.getDescription());
        dto.setShareToken(itinerary.getShareToken());
        dto.setCreatedAt(itinerary.getCreatedAt());
        dto.setUpdatedAt(itinerary.getUpdatedAt());
        
        // Convert events
        if (itinerary.getEvents() != null) {
            dto.setEvents(itinerary.getEvents().stream()
                .map(this::convertEventToDTO)
                .collect(Collectors.toList()));
            
            // Check for overlaps
            dto.setHasOverlaps(checkForOverlaps(itinerary.getEvents()));
            
            // Calculate total duration
            dto.setTotalDuration(calculateTotalDuration(itinerary.getEvents()));
        }
        
        dto.setShareUrl("tiranaevents://itinerary/" + itinerary.getShareToken());
        
        return dto;
    }

    private boolean checkForOverlaps(List<Event> events) {
        for (int i = 0; i < events.size() - 1; i++) {
            Event e1 = events.get(i);
            Event e2 = events.get(i + 1);
            
            if (e1.getEndTime() != null && e2.getStartTime() != null) {
                if (e1.getEndTime().isAfter(e2.getStartTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer calculateTotalDuration(List<Event> events) {
        int total = 0;
        for (Event event : events) {
            if (event.getStartTime() != null && event.getEndTime() != null) {
                total += (int) ChronoUnit.MINUTES.between(event.getStartTime(), event.getEndTime());
            }
        }
        return total;
    }

    private EventDTO convertEventToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setImageUrl(event.getImageUrl());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setVenue(event.getVenue());
        dto.setPrice(event.getPrice());
        return dto;
    }
}
