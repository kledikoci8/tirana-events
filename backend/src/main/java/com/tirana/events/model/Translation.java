package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "translations")
@Data
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType; // EVENT, CATEGORY, etc.

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String fieldName; // name, description

    @Column(nullable = false)
    private String language; // sq (Albanian), en (English)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String translatedText;
}
