package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event_bundles")
@Data
public class EventBundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String type; // RESTAURANT, TRANSPORT, WEATHER_ALTERNATIVE

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String externalUrl;
    private String imageUrl;
    private Double price;
    private Double distance; // in km
    private String address;
    private Double rating;

    // For transport
    private String transportType; // BUS, TAXI, WALKING
    private Integer estimatedMinutes;

    // For weather alternatives
    @ManyToOne
    @JoinColumn(name = "alternative_event_id")
    private Event alternativeEvent;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isActive = true;
}
