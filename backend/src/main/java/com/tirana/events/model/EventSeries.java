package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event_series")
@Data
public class EventSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    private String name; // "Summer Music Festival 2024"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverImageUrl;

    @OneToMany(mappedBy = "series")
    private List<Event> events;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isActive = true;
}
