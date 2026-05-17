package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventTemplateDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String venue;
    private Double latitude;
    private Double longitude;
    private Double price;
    private Boolean isFree;
    private Integer capacity;
    private Boolean isOutdoor;
    private Boolean wheelchairAccessible;
    private Boolean hearingLoopAvailable;
    private Boolean seatedVenue;
    private String additionalInfo;
    private LocalDateTime createdAt;
    private Integer timesUsed;
}
