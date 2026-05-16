package com.tirana.events.dto;

import lombok.Data;

@Data
public class EventBundleDTO {
    private Long id;
    private String type;
    private String title;
    private String description;
    private String externalUrl;
    private String imageUrl;
    private Double price;
    private Double distance;
    private String address;
    private Double rating;
    private String transportType;
    private Integer estimatedMinutes;
    private Long alternativeEventId;
    private String alternativeEventName;
}
