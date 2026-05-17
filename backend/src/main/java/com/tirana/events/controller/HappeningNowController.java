package com.tirana.events.controller;

import com.tirana.events.dto.HappeningNowDTO;
import com.tirana.events.service.HappeningNowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/happening-now")
@RequiredArgsConstructor
public class HappeningNowController {
    private final HappeningNowService happeningNowService;

    @GetMapping
    public ResponseEntity<List<HappeningNowDTO>> getHappeningNow(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        return ResponseEntity.ok(happeningNowService.getHappeningNowEvents(lat, lon));
    }
}
