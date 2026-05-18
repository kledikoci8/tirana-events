package com.tirana.events.controller;

import com.tirana.events.dto.CreateReviewRequest;
import com.tirana.events.dto.EventReviewDTO;
import com.tirana.events.service.ReviewService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final CurrentUserService currentUserService;
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<EventReviewDTO> createReview(
            @RequestBody CreateReviewRequest request,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(reviewService.createReview(userId, request));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<EventReviewDTO>> getEventReviews(@PathVariable Long eventId) {
        return ResponseEntity.ok(reviewService.getEventReviews(eventId));
    }

    @GetMapping("/events/{eventId}/verified")
    public ResponseEntity<List<EventReviewDTO>> getVerifiedReviews(@PathVariable Long eventId) {
        return ResponseEntity.ok(reviewService.getVerifiedReviews(eventId));
    }

    @GetMapping("/events/{eventId}/rating")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Long eventId) {
        return ResponseEntity.ok(Map.of("averageRating", reviewService.getAverageRating(eventId)));
    }

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<Void> addOrganizerReply(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        reviewService.addOrganizerReply(reviewId, userId, body.get("reply"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markHelpful(@PathVariable Long reviewId) {
        reviewService.markHelpful(reviewId);
        return ResponseEntity.ok().build();
    }
}
