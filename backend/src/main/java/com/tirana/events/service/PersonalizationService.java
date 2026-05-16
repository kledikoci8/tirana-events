package com.tirana.events.service;

import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalizationService {
    
    private final UserInteractionRepository interactionRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    
    /**
     * Track user interaction with an event
     */
    @Transactional
    public void trackInteraction(User user, Event event, UserInteraction.InteractionType type) {
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setEvent(event);
        interaction.setType(type);
        interaction.setTimestamp(LocalDateTime.now());
        interactionRepository.save(interaction);
    }
    
    /**
     * Get personalized feed for user
     */
    public List<Event> getPersonalizedFeed(User user, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next48Hours = now.plusHours(48);
        
        // Get user preferences
        Optional<UserPreference> prefOpt = preferenceRepository.findByUser(user);
        
        // If onboarding not completed, return general popular events
        if (prefOpt.isEmpty() || !prefOpt.get().getOnboardingCompleted()) {
            return eventRepository.findUpcomingEvents(now).stream()
                .limit(limit)
                .collect(Collectors.toList());
        }
        
        UserPreference pref = prefOpt.get();
        
        // Build personalized feed with weighted scoring
        Map<Event, Double> eventScores = new HashMap<>();
        
        // 1. Get events from user's selected categories (weight: 3.0)
        Set<Category> selectedCategories = pref.getSelectedCategories();
        if (!selectedCategories.isEmpty()) {
            List<Event> categoryEvents = eventRepository.findByCategoryInAndStartDateAfter(
                new ArrayList<>(selectedCategories), now
            );
            categoryEvents.forEach(event -> 
                eventScores.merge(event, 3.0, Double::sum)
            );
        }
        
        // 2. Get events based on user interaction history (weight: 2.0)
        List<Object[]> topCategories = interactionRepository.findTopCategoriesByUser(user);
        if (!topCategories.isEmpty()) {
            List<Long> categoryIds = topCategories.stream()
                .limit(3)
                .map(arr -> (Long) arr[0])
                .collect(Collectors.toList());
            
            List<Event> historyEvents = eventRepository.findByCategoryIdInAndStartDateAfter(categoryIds, now);
            historyEvents.forEach(event -> 
                eventScores.merge(event, 2.0, Double::sum)
            );
        }
        
        // 3. Collaborative filtering - events liked by similar users (weight: 2.5)
        List<Event> collaborativeEvents = interactionRepository.findCollaborativeFilteredEvents(user, now);
        collaborativeEvents.forEach(event -> 
            eventScores.merge(event, 2.5, Double::sum)
        );
        
        // 4. Apply recency bias - boost events in next 48 hours (weight: +4.0)
        eventScores.forEach((event, score) -> {
            if (event.getStartDate().isBefore(next48Hours)) {
                eventScores.put(event, score + 4.0);
            }
        });
        
        // 5. Penalize already viewed events (weight: -1.0)
        List<UserInteraction> recentViews = interactionRepository.findByUserAndTimestampAfter(
            user, now.minusDays(7)
        );
        recentViews.stream()
            .filter(ui -> ui.getType() == UserInteraction.InteractionType.VIEW)
            .forEach(ui -> eventScores.computeIfPresent(ui.getEvent(), (e, score) -> score - 1.0));
        
        // 6. Boost events near user's home location if set (weight: +1.5)
        if (pref.getHomeLatitude() != null && pref.getHomeLongitude() != null) {
            eventScores.forEach((event, score) -> {
                if (event.getLatitude() != null && event.getLongitude() != null) {
                    double distance = calculateDistance(
                        pref.getHomeLatitude(), pref.getHomeLongitude(),
                        event.getLatitude(), event.getLongitude()
                    );
                    if (distance < 5.0) { // Within 5km
                        eventScores.put(event, score + 1.5);
                    }
                }
            });
        }
        
        // Sort by score and return top events
        return eventScores.entrySet().stream()
            .sorted(Map.Entry.<Event, Double>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculate distance between two coordinates in kilometers
     */
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
     * Complete onboarding with selected categories
     */
    @Transactional
    public UserPreference completeOnboarding(User user, Set<Category> selectedCategories) {
        UserPreference pref = preferenceRepository.findByUser(user)
            .orElse(new UserPreference());
        
        pref.setUser(user);
        pref.setOnboardingCompleted(true);
        pref.setSelectedCategories(selectedCategories);
        
        return preferenceRepository.save(pref);
    }
    
    /**
     * Check if user needs onboarding
     */
    public boolean needsOnboarding(User user) {
        return preferenceRepository.findByUser(user)
            .map(pref -> !pref.getOnboardingCompleted())
            .orElse(true);
    }
}
