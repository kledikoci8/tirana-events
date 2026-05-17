package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WeatherForecastDTO {
    private LocalDateTime forecastDate;
    private Double temperature;
    private String condition;
    private Integer precipitationChance;
    private Double windSpeed;
    private Integer humidity;
    private String weatherIcon;
    private String warning; // "High chance of rain - consider indoor alternatives"
}
