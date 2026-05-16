package com.tirana.events.service;

import com.tirana.events.dto.EventFilterRequest;
import com.tirana.events.model.Event;
import com.tirana.events.model.FilterPreset;
import com.tirana.events.model.User;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.FilterPresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {
    
    private final EventRepository eventRepository;
    private final FilterPresetRepository presetRepository;
    private final EntityManager entityManager;
    
    /**
     * Filter events based on criteria
     */
    public List<Event> filterEvents(EventFilterRequest filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Only future events
        predicates.add(cb.greaterThan(event.get("startDate"), LocalDateTime.now()));
        
        // Price filter
        if (filter.getIncludeFree() != null && filter.getIncludeFree()) {
            predicates.add(cb.or(
                cb.isTrue(event.get("isFree")),
                cb.isNull(event.get("price"))
            ));
        } else {
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(event.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(event.get("price"), filter.getMaxPrice()));
            }
        }
        
        // Time of day filter
        if (filter.getStartHour() != null && filter.getEndHour() != null) {
            // This is a simplified version - in production, you'd need to extract hour from startDate
            // For now, we'll filter in memory after the query
        }
        
        // Date range filter
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (filter.getDateRangeType() != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (filter.getDateRangeType()) {
                case "TODAY":
                    startDate = now.toLocalDate().atStartOfDay();
                    endDate = now.toLocalDate().atTime(LocalTime.MAX);
                    break;
                case "THIS_WEEKEND":
                    // Find next Saturday and Sunday
                    LocalDateTime nextSaturday = now;
                    while (nextSaturday.getDayOfWeek() != DayOfWeek.SATURDAY) {
                        nextSaturday = nextSaturday.plusDays(1);
                    }
                    startDate = nextSaturday.toLocalDate().atStartOfDay();
                    endDate = nextSaturday.plusDays(1).toLocalDate().atTime(LocalTime.MAX);
                    break;
                case "THIS_WEEK":
                    startDate = now;
                    endDate = now.plusWeeks(1);
                    break;
                case "CUSTOM":
                    startDate = filter.getCustomStartDate();
                    endDate = filter.getCustomEndDate();
                    break;
            }
        }
        
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(event.get("startDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(event.get("startDate"), endDate));
        }
        
        // Accessibility filters
        if (filter.getRequireWheelchairAccess() != null && filter.getRequireWheelchairAccess()) {
            predicates.add(cb.isTrue(event.get("wheelchairAccessible")));
        }
        if (filter.getRequireHearingLoop() != null && filter.getRequireHearingLoop()) {
            predicates.add(cb.isTrue(event.get("hearingLoopAvailable")));
        }
        if (filter.getRequireSeatedVenue() != null && filter.getRequireSeatedVenue()) {
            predicates.add(cb.isTrue(event.get("seatedVenue")));
        }
        
        // Indoor/Outdoor filter
        if (filter.getIndoorOnly() != null && filter.getIndoorOnly()) {
            predicates.add(cb.isFalse(event.get("isOutdoor")));
        }
        if (filter.getOutdoorOnly() != null && filter.getOutdoorOnly()) {
            predicates.add(cb.isTrue(event.get("isOutdoor")));
        }
        
        // Category filter
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            predicates.add(event.get("category").get("id").in(filter.getCategoryIds()));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(event.get("startDate")));
        
        List<Event> results = entityManager.createQuery(query).getResultList();
        
        // Post-process for distance and time of day filters
        results = results.stream()
            .filter(e -> passesDistanceFilter(e, filter))
            .filter(e -> passesTimeOfDayFilter(e, filter))
            .collect(Collectors.toList());
        
        // Apply pagination
        int page = filter.getPage() != null ? filter.getPage() : 0;
        int size = filter.getSize() != null ? filter.getSize() : 20;
        int start = page * size;
        int end = Math.min(start + size, results.size());
        
        if (start >= results.size()) {
            return List.of();
        }
        
        return results.subList(start, end);
    }
    
    private boolean passesDistanceFilter(Event event, EventFilterRequest filter) {
        if (filter.getMaxDistance() == null || filter.getUserLatitude() == null || filter.getUserLongitude() == null) {
            return true;
        }
        
        if (event.getLatitude() == null || event.getLongitude() == null) {
            return false;
        }
        
        double distance = calculateDistance(
            filter.getUserLatitude(), filter.getUserLongitude(),
            event.getLatitude(), event.getLongitude()
        );
        
        return distance <= filter.getMaxDistance();
    }
    
    private boolean passesTimeOfDayFilter(Event event, EventFilterRequest filter) {
        if (filter.getStartHour() == null || filter.getEndHour() == null) {
            return true;
        }
        
        int eventHour = event.getStartDate().getHour();
        
        if (filter.getStartHour() <= filter.getEndHour()) {
            // Normal range (e.g., 9-17)
            return eventHour >= filter.getStartHour() && eventHour <= filter.getEndHour();
        } else {
            // Overnight range (e.g., 22-6)
            return eventHour >= filter.getStartHour() || eventHour <= filter.getEndHour();
        }
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Save filter preset
     */
    @Transactional
    public FilterPreset savePreset(User user, String name, EventFilterRequest filter) {
        if (presetRepository.existsByUserAndName(user, name)) {
            throw new RuntimeException("Preset with this name already exists");
        }
        
        FilterPreset preset = new FilterPreset();
        preset.setUser(user);
        preset.setName(name);
        preset.setMinPrice(filter.getMinPrice());
        preset.setMaxPrice(filter.getMaxPrice());
        preset.setIncludeFree(filter.getIncludeFree());
        preset.setMaxDistance(filter.getMaxDistance());
        preset.setStartHour(filter.getStartHour());
        preset.setEndHour(filter.getEndHour());
        preset.setDateRangeType(filter.getDateRangeType());
        preset.setCustomStartDate(filter.getCustomStartDate());
        preset.setCustomEndDate(filter.getCustomEndDate());
        preset.setRequireWheelchairAccess(filter.getRequireWheelchairAccess());
        preset.setRequireHearingLoop(filter.getRequireHearingLoop());
        preset.setRequireSeatedVenue(filter.getRequireSeatedVenue());
        preset.setIndoorOnly(filter.getIndoorOnly());
        preset.setOutdoorOnly(filter.getOutdoorOnly());
        
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            preset.setCategoryIds(filter.getCategoryIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        }
        
        return presetRepository.save(preset);
    }
    
    /**
     * Get user's saved presets
     */
    public List<FilterPreset> getUserPresets(User user) {
        return presetRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Delete preset
     */
    @Transactional
    public void deletePreset(Long presetId, User user) {
        FilterPreset preset = presetRepository.findById(presetId)
            .orElseThrow(() -> new RuntimeException("Preset not found"));
        
        if (!preset.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this preset");
        }
        
        presetRepository.delete(preset);
    }
    
    /**
     * Convert preset to filter request
     */
    public EventFilterRequest presetToFilter(FilterPreset preset) {
        EventFilterRequest filter = new EventFilterRequest();
        filter.setMinPrice(preset.getMinPrice());
        filter.setMaxPrice(preset.getMaxPrice());
        filter.setIncludeFree(preset.getIncludeFree());
        filter.setMaxDistance(preset.getMaxDistance());
        filter.setStartHour(preset.getStartHour());
        filter.setEndHour(preset.getEndHour());
        filter.setDateRangeType(preset.getDateRangeType());
        filter.setCustomStartDate(preset.getCustomStartDate());
        filter.setCustomEndDate(preset.getCustomEndDate());
        filter.setRequireWheelchairAccess(preset.getRequireWheelchairAccess());
        filter.setRequireHearingLoop(preset.getRequireHearingLoop());
        filter.setRequireSeatedVenue(preset.getRequireSeatedVenue());
        filter.setIndoorOnly(preset.getIndoorOnly());
        filter.setOutdoorOnly(preset.getOutdoorOnly());
        
        if (preset.getCategoryIds() != null && !preset.getCategoryIds().isEmpty()) {
            filter.setCategoryIds(
                java.util.Arrays.stream(preset.getCategoryIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList())
            );
        }
        
        return filter;
    }
}
