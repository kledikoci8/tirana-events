package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event_reviews")
@Data
public class EventReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ElementCollection
    @CollectionTable(name = "review_vibe_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag")
    private List<String> vibeTags; // "Too crowded", "Great music", "Good vibes", etc.

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isVerifiedAttendee = false;
    private Integer helpfulCount = 0;

    // Organizer reply
    @Column(columnDefinition = "TEXT")
    private String organizerReply;
    private LocalDateTime organizerRepliedAt;
}
