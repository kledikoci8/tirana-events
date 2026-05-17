package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SavedItineraryDTO {
    private Long id;
    private String name;
    private String description;
    private List<EventDTO> events;
    private String shareToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalDuration; // minutes
    private Boolean hasOverlaps;
    private String shareUrl;
}
