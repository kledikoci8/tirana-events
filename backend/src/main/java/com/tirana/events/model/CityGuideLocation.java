package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "city_guide_locations")
@Data
public class CityGuideLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // BUS_STOP, RESTAURANT, LANDMARK, PARKING

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;
    private Double rating;
    private String priceRange; // $, $$, $$$

    private Boolean isActive = true;
}
