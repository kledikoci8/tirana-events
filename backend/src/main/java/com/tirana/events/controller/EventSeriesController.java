package com.tirana.events.controller;

import com.tirana.events.dto.EventSeriesDTO;
import com.tirana.events.service.EventSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class EventSeriesController {
    private final EventSeriesService seriesService;

    @GetMapping
    public ResponseEntity<List<EventSeriesDTO>> getAllSeries() {
        return ResponseEntity.ok(seriesService.getAllSeries());
    }
}
