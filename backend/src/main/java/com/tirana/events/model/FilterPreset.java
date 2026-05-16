package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "filter_presets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterPreset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    // Price filter
    private Double minPrice;
    private Double maxPrice;
    private Boolean includeFree;
    
    // Distance filter (in km)
    private Double maxDistance;
    
    // Time of day filter
    private Integer startHour; // 0-23
    private Integer endHour;   // 0-23
    
    // Date range
    private String dateRangeType; // TODAY, THIS_WEEKEND, THIS_WEEK, CUSTOM
    private LocalDateTime customStartDate;
    private LocalDateTime customEndDate;
    
    // Accessibility
    private Boolean requireWheelchairAccess;
    private Boolean requireHearingLoop;
    private Boolean requireSeatedVenue;
    
    // Indoor/Outdoor
    private Boolean indoorOnly;
    private Boolean outdoorOnly;
    
    // Categories (stored as comma-separated IDs)
    private String categoryIds;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
