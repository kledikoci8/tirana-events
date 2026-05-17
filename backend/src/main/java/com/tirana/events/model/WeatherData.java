package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime forecastDate;

    @Column(nullable = false)
    private Double temperature; // Celsius

    @Column(nullable = false)
    private String condition; // CLEAR, CLOUDY, RAIN, STORM, SNOW

    private Integer precipitationChance; // 0-100%
    private Double windSpeed; // km/h
    private Integer humidity; // 0-100%

    private String weatherIcon; // Icon code

    @Column(nullable = false)
    private LocalDateTime fetchedAt = LocalDateTime.now();
}
