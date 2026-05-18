package com.tirana.events.controller;

import com.tirana.events.dto.UserBadgeDTO;
import com.tirana.events.service.BadgeService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {
    private final CurrentUserService currentUserService;
    private final BadgeService badgeService;

    @GetMapping("/my-badges")
    public ResponseEntity<List<UserBadgeDTO>> getMyBadges(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }

    @PostMapping("/check")
    public ResponseEntity<Void> checkBadges(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        badgeService.checkAndAwardBadges(userId);
        return ResponseEntity.ok().build();
    }
}
