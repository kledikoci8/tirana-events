package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "mood_searches")
@Data
public class MoodSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String query; // "I feel like dancing tonight"

    @Column(nullable = false)
    private String detectedMood; // ENERGETIC, CHILL, SOCIAL, CULTURAL, ROMANTIC, ADVENTUROUS

    @Column(columnDefinition = "TEXT")
    private String extractedKeywords; // JSON array of keywords

    @Column(nullable = false)
    private LocalDateTime searchedAt = LocalDateTime.now();

    private Integer resultsCount;
}
