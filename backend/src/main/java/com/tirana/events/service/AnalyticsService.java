package com.tirana.events.service;

import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final EventAnalyticsRepository analyticsRepository;
    private final UserInteractionRepository interactionRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    
    /**
     * Get or create analytics for an event
     */
    @Transactional
    public EventAnalytics getOrCreateAnalytics(Event event) {
        return analyticsRepository.findByEvent(event)
            .orElseGet(() -> {
                EventAnalytics analytics = new EventAnalytics();
                analytics.setEvent(event);
                return analyticsRepository.save(analytics);
            });
    }
    
    /**
     * Track event view
     */
    @Transactional
    public void trackView(Event event, User user, String source) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        analytics.setTotalViews(analytics.getTotalViews() + 1);
        
        // Track traffic source
        switch (source.toUpperCase()) {
            case "SEARCH":
                analytics.setFromSearch(analytics.getFromSearch() + 1);
                break;
            case "HOME_FEED":
                analytics.setFromHomeFeed(analytics.getFromHomeFeed() + 1);
                break;
            case "MAP":
                analytics.setFromMap(analytics.getFromMap() + 1);
                break;
            case "FRIEND_SHARE":
                analytics.setFromFriendShare(analytics.getFromFriendShare() + 1);
                break;
            case "DIRECT_LINK":
                analytics.setFromDirectLink(analytics.getFromDirectLink() + 1);
                break;
        }
        
        analyticsRepository.save(analytics);
    }
    
    /**
     * Track event save
     */
    @Transactional
    public void trackSave(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        analytics.setTotalSaves(analytics.getTotalSaves() + 1);
        analyticsRepository.save(analytics);
    }
    
    /**
     * Track ticket page view
     */
    @Transactional
    public void trackTicketPageView(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        analytics.setTicketPageViews(analytics.getTicketPageViews() + 1);
        analyticsRepository.save(analytics);
    }
    
    /**
     * Track purchase attempt
     */
    @Transactional
    public void trackPurchaseAttempt(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        analytics.setPurchaseAttempts(analytics.getPurchaseAttempts() + 1);
        analyticsRepository.save(analytics);
    }
    
    /**
     * Track completed purchase
     */
    @Transactional
    public void trackCompletedPurchase(Event event, Double amount) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        analytics.setCompletedPurchases(analytics.getCompletedPurchases() + 1);
        analytics.setTotalRevenue(analytics.getTotalRevenue() + amount);
        analyticsRepository.save(analytics);
    }
    
    /**
     * Update analytics (called by scheduler)
     */
    @Transactional
    public void updateAnalytics(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        
        // Calculate unique views
        List<UserInteraction> views = interactionRepository.findByUserAndTimestampAfter(
            null, LocalDateTime.now().minusDays(30)
        );
        Set<Long> uniqueUsers = views.stream()
            .filter(ui -> ui.getEvent().getId().equals(event.getId()))
            .map(ui -> ui.getUser().getId())
            .collect(Collectors.toSet());
        analytics.setUniqueViews((long) uniqueUsers.size());
        
        // Calculate demographics
        List<Ticket> tickets = ticketRepository.findByEvent(event);
        analytics.setAgeGroupDistribution(calculateAgeDistribution(tickets));
        analytics.setNeighborhoodDistribution(calculateNeighborhoodDistribution(tickets));
        analytics.setCategoryInterests(calculateCategoryInterests(tickets));
        
        // Calculate daily and weekly revenue
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayAgo = now.minusDays(1);
        LocalDateTime weekAgo = now.minusWeeks(1);
        
        List<Ticket> dailyTickets = tickets.stream()
            .filter(t -> t.getPurchaseDate().isAfter(dayAgo))
            .collect(Collectors.toList());
        
        List<Ticket> weeklyTickets = tickets.stream()
            .filter(t -> t.getPurchaseDate().isAfter(weekAgo))
            .collect(Collectors.toList());
        
        Double ticketPrice = event.getPrice() != null ? event.getPrice() : 0.0;
        analytics.setDailyRevenue(dailyTickets.size() * ticketPrice);
        analytics.setWeeklyRevenue(weeklyTickets.size() * ticketPrice);
        
        analyticsRepository.save(analytics);
    }
    
    /**
     * Get analytics overview
     */
    public Map<String, Object> getAnalyticsOverview(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalViews", analytics.getTotalViews());
        overview.put("uniqueViews", analytics.getUniqueViews());
        overview.put("totalSaves", analytics.getTotalSaves());
        overview.put("completedPurchases", analytics.getCompletedPurchases());
        overview.put("totalRevenue", analytics.getTotalRevenue());
        overview.put("dailyRevenue", analytics.getDailyRevenue());
        overview.put("weeklyRevenue", analytics.getWeeklyRevenue());
        overview.put("lastUpdated", analytics.getLastUpdated());
        
        return overview;
    }
    
    /**
     * Get sales funnel data
     */
    public Map<String, Object> getSalesFunnel(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        
        Map<String, Object> funnel = new HashMap<>();
        funnel.put("views", analytics.getTotalViews());
        funnel.put("saves", analytics.getTotalSaves());
        funnel.put("ticketPageViews", analytics.getTicketPageViews());
        funnel.put("purchaseAttempts", analytics.getPurchaseAttempts());
        funnel.put("completedPurchases", analytics.getCompletedPurchases());
        
        funnel.put("viewToSaveRate", analytics.getViewToSaveRate());
        funnel.put("saveToTicketRate", analytics.getSaveToTicketRate());
        funnel.put("ticketToPurchaseRate", analytics.getTicketToPurchaseRate());
        funnel.put("overallConversionRate", analytics.getOverallConversionRate());
        
        return funnel;
    }
    
    /**
     * Get traffic sources
     */
    public Map<String, Long> getTrafficSources(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        
        Map<String, Long> sources = new HashMap<>();
        sources.put("search", analytics.getFromSearch());
        sources.put("homeFeed", analytics.getFromHomeFeed());
        sources.put("map", analytics.getFromMap());
        sources.put("friendShare", analytics.getFromFriendShare());
        sources.put("directLink", analytics.getFromDirectLink());
        
        return sources;
    }
    
    /**
     * Export analytics to CSV
     */
    public String exportToCSV(Event event) {
        EventAnalytics analytics = getOrCreateAnalytics(event);
        
        StringWriter writer = new StringWriter();
        writer.append("Metric,Value\n");
        writer.append("Event Name,").append(event.getName()).append("\n");
        writer.append("Total Views,").append(analytics.getTotalViews().toString()).append("\n");
        writer.append("Unique Views,").append(analytics.getUniqueViews().toString()).append("\n");
        writer.append("Total Saves,").append(analytics.getTotalSaves().toString()).append("\n");
        writer.append("Ticket Page Views,").append(analytics.getTicketPageViews().toString()).append("\n");
        writer.append("Purchase Attempts,").append(analytics.getPurchaseAttempts().toString()).append("\n");
        writer.append("Completed Purchases,").append(analytics.getCompletedPurchases().toString()).append("\n");
        writer.append("Total Revenue,").append(analytics.getTotalRevenue().toString()).append(" ALL\n");
        writer.append("Daily Revenue,").append(analytics.getDailyRevenue().toString()).append(" ALL\n");
        writer.append("Weekly Revenue,").append(analytics.getWeeklyRevenue().toString()).append(" ALL\n");
        writer.append("View to Save Rate,").append(String.format("%.2f", analytics.getViewToSaveRate())).append("%\n");
        writer.append("Overall Conversion Rate,").append(String.format("%.2f", analytics.getOverallConversionRate())).append("%\n");
        
        return writer.toString();
    }
    
    private String calculateAgeDistribution(List<Ticket> tickets) {
        // Simplified - in production, calculate from user birth dates
        return "{\"18-24\": 30, \"25-34\": 45, \"35-44\": 20, \"45+\": 5}";
    }
    
    private String calculateNeighborhoodDistribution(List<Ticket> tickets) {
        // Simplified - in production, calculate from user addresses
        return "{\"Blloku\": 25, \"Kombinat\": 20, \"Ekspozita\": 15, \"Other\": 40}";
    }
    
    private String calculateCategoryInterests(List<Ticket> tickets) {
        // Simplified - in production, calculate from user preferences
        return "{\"Music\": 50, \"Culture\": 30, \"University\": 15, \"Volunteering\": 5}";
    }
}
