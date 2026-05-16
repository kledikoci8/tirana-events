package com.tirana.events.scheduler;

import com.tirana.events.model.Event;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalyticsScheduler {
    
    private final AnalyticsService analyticsService;
    private final EventRepository eventRepository;
    
    /**
     * Update analytics for all upcoming events every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void updateEventAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusMonths(1);
        
        List<Event> upcomingEvents = eventRepository.findByStartDateBetween(now, futureLimit);
        
        for (Event event : upcomingEvents) {
            try {
                analyticsService.updateAnalytics(event);
            } catch (Exception e) {
                // Log error but continue with other events
                System.err.println("Failed to update analytics for event: " + event.getId());
            }
        }
    }
}
