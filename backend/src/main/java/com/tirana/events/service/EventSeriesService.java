package com.tirana.events.service;

import com.tirana.events.dto.EventSeriesDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSeriesService {
    private final EventSeriesRepository seriesRepository;
    private final UserRepository userRepository;

    public List<EventSeriesDTO> getAllSeries() {
        return seriesRepository.findByIsActiveTrueOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private EventSeriesDTO convertToDTO(EventSeries series) {
        EventSeriesDTO dto = new EventSeriesDTO();
        dto.setId(series.getId());
        dto.setName(series.getName());
        dto.setDescription(series.getDescription());
        dto.setCoverImageUrl(series.getCoverImageUrl());
        dto.setOrganizerId(series.getOrganizer().getId());
        dto.setOrganizerName(series.getOrganizer().getName());
        dto.setCreatedAt(series.getCreatedAt());
        dto.setTotalEvents(series.getEvents() != null ? series.getEvents().size() : 0);
        return dto;
    }
}
