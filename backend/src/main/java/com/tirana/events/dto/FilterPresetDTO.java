package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FilterPresetDTO {
    private Long id;
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private Boolean includeFree;
    private Double maxDistance;
    private Integer startHour;
    private Integer endHour;
    private String dateRangeType;
    private Boolean requireWheelchairAccess;
    private Boolean requireHearingLoop;
    private Boolean requireSeatedVenue;
    private Boolean indoorOnly;
    private Boolean outdoorOnly;
    private String categoryIds;
    private LocalDateTime createdAt;
}
