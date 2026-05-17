package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Data
public class UserBadge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String badgeCode; // CULTURE_VULTURE, NIGHT_OWL, TIRANA_ORIGINAL, etc.

    @Column(nullable = false)
    private String badgeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconUrl;

    @Column(nullable = false)
    private LocalDateTime earnedAt = LocalDateTime.now();

    private Boolean isDisplayed = true;
}
