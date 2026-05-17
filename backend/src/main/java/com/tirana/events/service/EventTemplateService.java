package com.tirana.events.service;

import com.tirana.events.dto.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventTemplateService {
    private final EventTemplateRepository templateRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public EventTemplateDTO createTemplate(Long organizerId, Event sourceEvent) {
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        EventTemplate template = new EventTemplate();
        template.setOrganizer(organizer);
        template.setName(sourceEvent.getName());
        template.setDescription(sourceEvent.getDescription());
        template.setCategory(sourceEvent.getCategory());
        template.setVenue(sourceEvent.getVenue());
        template.setLatitude(sourceEvent.getLatitude());
        template.setLongitude(sourceEvent.getLongitude());
        template.setPrice(sourceEvent.getPrice());
        template.setIsFree(sourceEvent.getIsFree());
        template.setCapacity(sourceEvent.getMaxAttendees());
        template.setIsOutdoor(sourceEvent.getIsOutdoor());
        template.setWheelchairAccessible(sourceEvent.getWheelchairAccessible());
        template.setHearingLoopAvailable(sourceEvent.getHearingLoopAvailable());
        template.setSeatedVenue(sourceEvent.getSeatedVenue());
        template.setCreatedAt(LocalDateTime.now());
        template.setTimesUsed(0);
        
        template = templateRepository.save(template);
        
        return convertToDTO(template);
    }

    @Transactional
    public Event createEventFromTemplate(Long templateId, CreateEventFromTemplateRequest request) {
        EventTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));
        
        Event event = new Event();
        event.setName(template.getName());
        event.setDescription(template.getDescription());
        event.setCategory(template.getCategory());
        event.setLocation(template.getVenue());
        event.setVenue(template.getVenue());
        event.setLatitude(template.getLatitude());
        event.setLongitude(template.getLongitude());
        event.setStartDate(request.getStartTime());
        event.setStartTime(request.getStartTime());
        event.setEndDate(request.getEndTime());
        event.setEndTime(request.getEndTime());
        event.setImageUrl(request.getImageUrl());
        event.setPrice(template.getPrice());
        event.setIsFree(template.getIsFree());
        event.setMaxAttendees(template.getCapacity());
        event.setTicketsAvailable(template.getCapacity());
        event.setIsOutdoor(template.getIsOutdoor());
        event.setWheelchairAccessible(template.getWheelchairAccessible());
        event.setHearingLoopAvailable(template.getHearingLoopAvailable());
        event.setSeatedVenue(template.getSeatedVenue());
        event.setOrganizer(template.getOrganizer());
        event.setCreatedAt(LocalDateTime.now());
        
        event = eventRepository.save(event);
        
        // Update template usage count
        template.setTimesUsed(template.getTimesUsed() + 1);
        templateRepository.save(template);
        
        return event;
    }

    public List<EventTemplateDTO> getOrganizerTemplates(Long organizerId) {
        return templateRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<EventTemplateDTO> getPopularTemplates(Long organizerId) {
        return templateRepository.findByOrganizerIdOrderByTimesUsedDesc(organizerId)
            .stream()
            .limit(10)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTemplate(Long templateId, Long organizerId) {
        EventTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));
        
        if (!template.getOrganizer().getId().equals(organizerId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        templateRepository.delete(template);
    }

    private EventTemplateDTO convertToDTO(EventTemplate template) {
        EventTemplateDTO dto = new EventTemplateDTO();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setDescription(template.getDescription());
        
        if (template.getCategory() != null) {
            dto.setCategoryId(template.getCategory().getId());
            dto.setCategoryName(template.getCategory().getName());
        }
        
        dto.setVenue(template.getVenue());
        dto.setLatitude(template.getLatitude());
        dto.setLongitude(template.getLongitude());
        dto.setPrice(template.getPrice());
        dto.setIsFree(template.getIsFree());
        dto.setCapacity(template.getCapacity());
        dto.setIsOutdoor(template.getIsOutdoor());
        dto.setWheelchairAccessible(template.getWheelchairAccessible());
        dto.setHearingLoopAvailable(template.getHearingLoopAvailable());
        dto.setSeatedVenue(template.getSeatedVenue());
        dto.setAdditionalInfo(template.getAdditionalInfo());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setTimesUsed(template.getTimesUsed());
        
        return dto;
    }
}
