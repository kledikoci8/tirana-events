package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "curated_lists")
@Data
public class CuratedList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curator_id", nullable = false)
    private Curator curator;

    @Column(nullable = false)
    private String title; // "This Week in Tirana"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverImageUrl;

    @ManyToMany
    @JoinTable(
        name = "curated_list_events",
        joinColumns = @JoinColumn(name = "list_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

    private Integer viewsCount = 0;
    private Integer savesCount = 0;
    private Integer ticketsSold = 0;

    @Column(nullable = false)
    private Boolean isPublished = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;
}
