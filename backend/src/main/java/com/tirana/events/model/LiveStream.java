package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_streams")
@Data
public class LiveStream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String streamUrl;

    private String thumbnailUrl;

    @Column(nullable = false)
    private Boolean isLive = false;

    private Integer viewersCount = 0;
    private Integer reactionsCount = 0;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
