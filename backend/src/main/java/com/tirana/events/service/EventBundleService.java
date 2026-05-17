package com.tirana.events.service;

import com.tirana.events.dto.EventBundleDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventBundleService {
    private final EventBundleRepository bundleRepository;
    private final EventRepository eventRepository;

    public List<EventBundleDTO> getEventBundles(Long eventId) {
        return bundleRepository.findByEventIdAndIsActiveTrueOrderByDistanceAsc(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventBundleDTO> getRestaurantRecommendations(Long eventId) {
        return bundleRepository.findByEventIdAndTypeAndIsActiveTrue(eventId, "RESTAURANT")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventBundleDTO> getTransportOptions(Long eventId) {
        return bundleRepository.findByEventIdAndTypeAndIsActiveTrue(eventId, "TRANSPORT")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventBundleDTO> getWeatherAlternatives(Long eventId) {
        return bundleRepository.findByEventIdAndTypeAndIsActiveTrue(eventId, "WEATHER_ALTERNATIVE")
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EventBundleDTO convertToDTO(EventBundle bundle) {
        EventBundleDTO dto = new EventBundleDTO();
        dto.setId(bundle.getId());
        dto.setType(bundle.getType());
        dto.setTitle(bundle.getTitle());
        dto.setDescription(bundle.getDescription());
        dto.setExternalUrl(bundle.getExternalUrl());
        dto.setImageUrl(bundle.getImageUrl());
        dto.setPrice(bundle.getPrice());
        dto.setDistance(bundle.getDistance());
        dto.setAddress(bundle.getAddress());
        dto.setRating(bundle.getRating());
        dto.setTransportType(bundle.getTransportType());
        dto.setEstimatedMinutes(bundle.getEstimatedMinutes());
        
        if (bundle.getAlternativeEvent() != null) {
            dto.setAlternativeEventId(bundle.getAlternativeEvent().getId());
            dto.setAlternativeEventName(bundle.getAlternativeEvent().getName());
        }
        
        return dto;
    }
}
