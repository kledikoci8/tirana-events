package com.tirana.events.controller;

import com.tirana.events.model.Event;
import com.tirana.events.model.User;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/events/{eventId}/overview")
    public ResponseEntity<Map<String, Object>> getOverview(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        Map<String, Object> overview = analyticsService.getAnalyticsOverview(event);
        
        return ResponseEntity.ok(overview);
    }
    
    @GetMapping("/events/{eventId}/funnel")
    public ResponseEntity<Map<String, Object>> getSalesFunnel(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        Map<String, Object> funnel = analyticsService.getSalesFunnel(event);
        
        return ResponseEntity.ok(funnel);
    }
    
    @GetMapping("/events/{eventId}/traffic")
    public ResponseEntity<Map<String, Long>> getTrafficSources(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        Map<String, Long> traffic = analyticsService.getTrafficSources(event);
        
        return ResponseEntity.ok(traffic);
    }
    
    @GetMapping("/events/{eventId}/demographics")
    public ResponseEntity<Map<String, Object>> getDemographics(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        
        // In production, parse JSON from analytics
        Map<String, Object> demographics = Map.of(
            "ageGroups", Map.of("18-24", 30, "25-34", 45, "35-44", 20, "45+", 5),
            "neighborhoods", Map.of("Blloku", 25, "Kombinat", 20, "Ekspozita", 15, "Other", 40),
            "interests", Map.of("Music", 50, "Culture", 30, "University", 15, "Volunteering", 5)
        );
        
        return ResponseEntity.ok(demographics);
    }
    
    @GetMapping("/events/{eventId}/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        Map<String, Object> overview = analyticsService.getAnalyticsOverview(event);
        
        Map<String, Object> revenue = Map.of(
            "total", overview.get("totalRevenue"),
            "daily", overview.get("dailyRevenue"),
            "weekly", overview.get("weeklyRevenue"),
            "ticketsSold", overview.get("completedPurchases")
        );
        
        return ResponseEntity.ok(revenue);
    }
    
    @GetMapping("/events/{eventId}/export")
    public ResponseEntity<String> exportCSV(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        Event event = getEventAndVerifyOrganizer(authentication, eventId);
        String csv = analyticsService.exportToCSV(event);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "event-" + eventId + "-analytics.csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csv);
    }
    
    @PostMapping("/events/{eventId}/track/view")
    public ResponseEntity<Void> trackView(
            Authentication authentication,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "DIRECT_LINK") String source) {
        
        User user = getCurrentUser(authentication);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        analyticsService.trackView(event, user, source);
        
        return ResponseEntity.ok().build();
    }
    
    private Event getEventAndVerifyOrganizer(Authentication authentication, Long eventId) {
        User user = getCurrentUser(authentication);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new RuntimeException("Only event organizer can view analytics");
        }
        
        return event;
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
