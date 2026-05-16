package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "dynamic_prices")
@Data
public class DynamicPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String priceType; // EARLY_BIRD, REGULAR, LAST_MINUTE, SURGE

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private Integer ticketsRemaining;
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
