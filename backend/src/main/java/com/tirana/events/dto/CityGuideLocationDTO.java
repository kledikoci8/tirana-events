package com.tirana.events.dto;

import lombok.Data;

@Data
public class CityGuideLocationDTO {
    private Long id;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String address;
    private String description;
    private String imageUrl;
    private Double rating;
    private String priceRange;
    private Double distance; // Distance from event in km
    private Integer walkingMinutes;
}
