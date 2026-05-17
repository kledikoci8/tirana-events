package com.tirana.events.controller;

import com.tirana.events.dto.WeatherForecastDTO;
import com.tirana.events.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/forecast")
    public ResponseEntity<List<WeatherForecastDTO>> get5DayForecast() {
        return ResponseEntity.ok(weatherService.get5DayForecast());
    }

    @GetMapping("/date")
    public ResponseEntity<WeatherForecastDTO> getWeatherForDate(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return ResponseEntity.ok(weatherService.getWeatherForDate(dateTime));
    }
}
