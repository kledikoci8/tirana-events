package com.tirana.events.service;

import com.tirana.events.dto.CreateEventRequest;
import com.tirana.events.dto.EventDTO;
import com.tirana.events.model.Category;
import com.tirana.events.model.Event;
import com.tirana.events.model.User;
import com.tirana.events.repository.CategoryRepository;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public EventDTO createEvent(CreateEventRequest request, String userEmail) {
        User organizer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setImageUrl(request.getImageUrl());
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setMaxAttendees(request.getMaxAttendees());
        
        event = eventRepository.save(event);
        
        return convertToDTO(event, organizer);
    }
    
    public List<EventDTO> getUpcomingEvents(String userEmail) {
        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return events.stream().map(e -> convertToDTO(e, user)).collect(Collectors.toList());
    }
    
    public List<EventDTO> searchEvents(String query, String userEmail) {
        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        List<Event> events = eventRepository.searchEvents(query);
        return events.stream().map(e -> convertToDTO(e, user)).collect(Collectors.toList());
    }
    
    public EventDTO getEventById(Long id, String userEmail) {
        User user = userEmail != null ? userRepository.findByEmail(userEmail).orElse(null) : null;
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return convertToDTO(event, user);
    }
    
    public void saveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        user.getSavedEvents().add(event);
        userRepository.save(user);
    }
    
    public void unsaveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        user.getSavedEvents().remove(event);
        userRepository.save(user);
    }
    
    private EventDTO convertToDTO(Event event, User user) {
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
        dto.setCategoryId(event.getCategory().getId());
        dto.setCategoryName(event.getCategory().getName());
        dto.setOrganizerId(event.getOrganizer().getId());
        dto.setOrganizerName(event.getOrganizer().getFullName());
        dto.setMaxAttendees(event.getMaxAttendees());
        dto.setCurrentAttendees(event.getTickets().size());
        dto.setSaved(user != null && user.getSavedEvents().contains(event));
        return dto;
    }
}
