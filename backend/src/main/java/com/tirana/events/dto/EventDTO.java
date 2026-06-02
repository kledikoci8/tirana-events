package com.tirana.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Long organizerId;
    private String organizerName;
    private Integer maxAttendees;
    private Integer currentAttendees;
    
    // FIX BUG #2: Change JSON property to "isSaved" to match frontend expectations
    // Frontend uses item.isSaved everywhere, so backend JSON should match
    @JsonProperty("isSaved")
    private boolean isSaved;
    
    private LocalDateTime createdAt;
    
    // Additional fields for specific features
    private Double price;
    private Boolean isFree;
    private String venue;
    // CLEANUP #2: Removed startTime and endTime fields
    // They were always null because Event model doesn't have these fields
    // Use startDate and endDate instead
    private Boolean isOutdoor;
}
