package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tiers")
@Data
public class UserTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String tier; // BRONZE, SILVER, GOLD, VIP

    @Column(nullable = false)
    private Integer totalPoints = 0;

    @Column(nullable = false)
    private Integer lifetimePoints = 0;

    private Integer currentStreak = 0; // Days in a row attending events
    private Integer longestStreak = 0;

    private LocalDateTime lastEventAttended;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime tierAchievedAt = LocalDateTime.now();
}
