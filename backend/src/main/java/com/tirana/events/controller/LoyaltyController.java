package com.tirana.events.controller;

import com.tirana.events.dto.LoyaltyPointsDTO;
import com.tirana.events.dto.UserTierDTO;
import com.tirana.events.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    @GetMapping("/tier")
    public ResponseEntity<UserTierDTO> getUserTier(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(loyaltyService.getUserTier(userId));
    }

    @GetMapping("/points/history")
    public ResponseEntity<List<LoyaltyPointsDTO>> getPointsHistory(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(loyaltyService.getUserPointsHistory(userId));
    }

    @PostMapping("/points/redeem")
    public ResponseEntity<Void> redeemPoints(
            @RequestBody Map<String, Integer> body,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        loyaltyService.redeemPoints(userId, body.get("points"));
        return ResponseEntity.ok().build();
    }
}
