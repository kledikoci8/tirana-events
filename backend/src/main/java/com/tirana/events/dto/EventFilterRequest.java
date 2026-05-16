package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterRequest {
    // Price filter
    private Double minPrice;
    private Double maxPrice;
    private Boolean includeFree;
    
    // Distance filter (in km from user's location)
    private Double maxDistance;
    private Double userLatitude;
    private Double userLongitude;
    
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
    
    // Categories
    private List<Long> categoryIds;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 20;
}
