package com.tirana.events.controller;

import com.tirana.events.dto.DynamicPriceDTO;
import com.tirana.events.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class DynamicPricingController {
    private final DynamicPricingService pricingService;

    @GetMapping("/events/{eventId}/current")
    public ResponseEntity<DynamicPriceDTO> getCurrentPrice(@PathVariable Long eventId) {
        return ResponseEntity.ok(pricingService.getCurrentPrice(eventId));
    }

    @GetMapping("/events/{eventId}/history")
    public ResponseEntity<List<DynamicPriceDTO>> getPriceHistory(@PathVariable Long eventId) {
        return ResponseEntity.ok(pricingService.getPriceHistory(eventId));
    }

    @PostMapping("/events/{eventId}/early-bird")
    public ResponseEntity<Void> createEarlyBird(
            @PathVariable Long eventId,
            @RequestBody Map<String, Object> body) {
        Double discount = Double.parseDouble(body.get("discountPercentage").toString());
        String until = body.get("validUntil").toString();
        pricingService.createEarlyBirdPricing(eventId, discount, java.time.LocalDateTime.parse(until));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/events/{eventId}/last-minute")
    public ResponseEntity<Void> createLastMinute(
            @PathVariable Long eventId,
            @RequestBody Map<String, Double> body) {
        pricingService.createLastMinuteDeal(eventId, body.get("discountPercentage"));
        return ResponseEntity.ok().build();
    }
}
