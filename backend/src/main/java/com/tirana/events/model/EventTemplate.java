package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_templates")
@Data
public class EventTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

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

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer timesUsed = 0;
}
