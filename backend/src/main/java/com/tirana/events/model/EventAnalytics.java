package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "event_id", unique = true, nullable = false)
    private Event event;
    
    // View metrics
    private Long totalViews = 0L;
    private Long uniqueViews = 0L;
    
    // Engagement metrics
    private Long totalSaves = 0L;
    private Long ticketPageViews = 0L;
    private Long purchaseAttempts = 0L;
    private Long completedPurchases = 0L;
    
    // Conversion rates (calculated)
    private Double viewToSaveRate = 0.0;
    private Double saveToTicketRate = 0.0;
    private Double ticketToPurchaseRate = 0.0;
    private Double overallConversionRate = 0.0;
    
    // Traffic sources
    private Long fromSearch = 0L;
    private Long fromHomeFeed = 0L;
    private Long fromMap = 0L;
    private Long fromFriendShare = 0L;
    private Long fromDirectLink = 0L;
    
    // Revenue
    private Double totalRevenue = 0.0;
    private Double dailyRevenue = 0.0;
    private Double weeklyRevenue = 0.0;
    
    // Demographics (stored as JSON or separate tables in production)
    private String ageGroupDistribution; // JSON: {"18-24": 30, "25-34": 45, ...}
    private String neighborhoodDistribution; // JSON: {"Blloku": 20, "Kombinat": 15, ...}
    private String categoryInterests; // JSON: {"Music": 50, "Culture": 30, ...}
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @PreUpdate
    public void calculateRates() {
        if (totalViews > 0) {
            viewToSaveRate = (totalSaves.doubleValue() / totalViews) * 100;
        }
        if (totalSaves > 0) {
            saveToTicketRate = (ticketPageViews.doubleValue() / totalSaves) * 100;
        }
        if (ticketPageViews > 0) {
            ticketToPurchaseRate = (completedPurchases.doubleValue() / ticketPageViews) * 100;
        }
        if (totalViews > 0) {
            overallConversionRate = (completedPurchases.doubleValue() / totalViews) * 100;
        }
        lastUpdated = LocalDateTime.now();
    }
}
