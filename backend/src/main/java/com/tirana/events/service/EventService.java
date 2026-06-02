package com.tirana.events.service;

import com.tirana.events.dto.CreateEventRequest;
import com.tirana.events.dto.EventDTO;
import com.tirana.events.dto.EventFilterRequest;
import com.tirana.events.exception.NotFoundException;
import com.tirana.events.model.Category;
import com.tirana.events.model.Event;
import com.tirana.events.model.User;
import com.tirana.events.repository.CategoryRepository;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.TicketRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.util.SqlUtil;
import com.tirana.events.util.SanitizationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final FilterService filterService;
    private final PersonalizationService personalizationService;

    public EventService(EventRepository eventRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        TicketRepository ticketRepository,
                        FilterService filterService,
                        PersonalizationService personalizationService) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.filterService = filterService;
        this.personalizationService = personalizationService;
    }

    @Transactional
    public EventDTO createEvent(CreateEventRequest request, String userEmail) {
        User organizer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Event event = new Event();
        
        // FIX B4: Sanitize all user input to prevent XSS attacks
        event.setName(SanitizationUtil.sanitizeText(request.getName()));
        event.setDescription(SanitizationUtil.sanitizeHtml(request.getDescription()));
        event.setLocation(SanitizationUtil.sanitizeText(request.getLocation()));
        
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setImageUrl(request.getImageUrl());
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setMaxAttendees(request.getMaxAttendees());
        if (request.getMaxAttendees() != null) {
            event.setTicketsAvailable(request.getMaxAttendees());
        }

        event = eventRepository.save(event);
        return convertToDTO(event, organizer);
    }

    public List<EventDTO> getUpcomingEvents(String userEmail, int page, int size) {
        User user = resolveUser(userEmail);
        
        // FIX E1: Add pagination at database level using Spring Data Pageable
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = eventRepository.findUpcomingEventsWithPagination(
            LocalDateTime.now(), 
            pageable
        );
        
        return events.stream()
                .map(e -> convertToDTO(e, user))
                .collect(Collectors.toList());
    }

    public List<EventDTO> getRecommendedEvents(String userEmail, int limit) {
        User user = resolveUser(userEmail);
        if (user != null) {
            return personalizationService.getPersonalizedFeed(user, limit).stream()
                    .map(e -> convertToDTO(e, user))
                    .collect(Collectors.toList());
        }
        return getUpcomingEvents(userEmail, 0, limit);
    }

    public List<EventDTO> getNearbyEvents(double lat, double lng, double radiusKm, String userEmail) {
        User user = resolveUser(userEmail);
        EventFilterRequest filter = new EventFilterRequest();
        filter.setUserLatitude(lat);
        filter.setUserLongitude(lng);
        filter.setMaxDistance(radiusKm);
        filter.setPage(0);
        filter.setSize(100);
        return filterService.filterEvents(filter).stream()
                .map(e -> convertToDTO(e, user))
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByOrganizer(String userEmail) {
        User organizer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return eventRepository.findByOrganizerIdOrderByStartDateDesc(organizer.getId()).stream()
                .map(e -> convertToDTO(e, organizer))
                .collect(Collectors.toList());
    }

    public List<EventDTO> searchEvents(String query, String userEmail, int page, int size) {
        User user = resolveUser(userEmail);
        
        // FIX A4: Escape LIKE wildcards to prevent wildcard injection attacks
        String escapedQuery = SqlUtil.escapeLikePattern(query);
        
        // FIX E1: Add pagination at database level using Spring Data Pageable
        Pageable pageable = PageRequest.of(page, size);
        List<Event> events = eventRepository.searchEventsWithPagination(escapedQuery, pageable);
        
        return events.stream()
                .map(e -> convertToDTO(e, user))
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id, String userEmail) {
        User user = resolveUser(userEmail);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return convertToDTO(event, user);
    }

    public void saveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        user.getSavedEvents().add(event);
        userRepository.save(user);
    }

    public void unsaveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        user.getSavedEvents().remove(event);
        userRepository.save(user);
    }

    public EventDTO convertToDTO(Event event, User user) {
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
        dto.setCreatedAt(event.getCreatedAt());
        
        dto.setPrice(event.getPrice() != null ? event.getPrice() : 0.0);
        dto.setIsFree(event.getIsFree() != null ? event.getIsFree() : (event.getPrice() == null || event.getPrice() == 0.0));
        
        dto.setVenue(event.getVenue());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setIsOutdoor(event.getIsOutdoor());

        if (event.getCategory() != null) {
            dto.setCategoryId(event.getCategory().getId());
            dto.setCategoryName(event.getCategory().getName());
        }
        if (event.getOrganizer() != null) {
            dto.setOrganizerId(event.getOrganizer().getId());
            dto.setOrganizerName(event.getOrganizer().getFullName());
        }

        dto.setMaxAttendees(event.getMaxAttendees());
        
        Long attendeeCount = ticketRepository.countByEvent(event);
        dto.setCurrentAttendees(attendeeCount != null ? attendeeCount.intValue() : 0);
        
        dto.setSaved(user != null && user.getSavedEvents() != null && user.getSavedEvents().contains(event));
        
        return dto;
    }

    private User resolveUser(String userEmail) {
        if (userEmail == null) {
            return null;
        }
        return userRepository.findByEmail(userEmail).orElse(null);
    }
    
    // FIX A3: IDOR Protection - Only allow event updates by the organizer
    @Transactional
    public EventDTO updateEvent(Long eventId, CreateEventRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        
        // CRITICAL: Verify the user is the organizer
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new com.tirana.events.exception.ForbiddenException(
                "You can only update events that you organize");
        }
        
        // Update event fields
        event.setName(SanitizationUtil.sanitizeText(request.getName()));
        event.setDescription(SanitizationUtil.sanitizeHtml(request.getDescription()));
        event.setLocation(SanitizationUtil.sanitizeText(request.getLocation()));
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setImageUrl(request.getImageUrl());
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        
        event = eventRepository.save(event);
        return convertToDTO(event, user);
    }
    
    // FIX A3: IDOR Protection - Only allow event deletion by the organizer
    @Transactional
    public void deleteEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        
        // CRITICAL: Verify the user is the organizer
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new com.tirana.events.exception.ForbiddenException(
                "You can only delete events that you organize");
        }
        
        eventRepository.delete(event);
    }
}
