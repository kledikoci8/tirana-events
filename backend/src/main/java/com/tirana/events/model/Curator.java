package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "curators")
@Data
public class Curator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String avatarUrl;
    private String coverImageUrl;

    @Column(nullable = false)
    private Boolean isVerified = false;

    private Integer followersCount = 0;
    private Integer curatedListsCount = 0;
    private Integer totalTicketsSold = 0;

    @Column(nullable = false)
    private Double commissionRate = 5.0; // 5% commission on ticket sales

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
