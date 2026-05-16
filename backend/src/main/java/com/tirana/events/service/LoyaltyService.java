package com.tirana.events.service;

import com.tirana.events.dto.LoyaltyPointsDTO;
import com.tirana.events.dto.UserTierDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoyaltyService {
    private final LoyaltyPointsRepository pointsRepository;
    private final UserTierRepository tierRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // Point values
    private static final int POINTS_PER_TICKET = 100;
    private static final int POINTS_PER_REVIEW = 50;
    private static final int POINTS_PER_REFERRAL = 200;
    private static final int POINTS_PER_CHECKIN = 25;
    private static final int POINTS_PER_STREAK_DAY = 10;

    // Tier thresholds
    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 2000;
    private static final int VIP_THRESHOLD = 5000;

    @Transactional
    public void awardPoints(Long userId, Integer points, String action, String description, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LoyaltyPoints loyaltyPoints = new LoyaltyPoints();
        loyaltyPoints.setUser(user);
        loyaltyPoints.setPoints(points);
        loyaltyPoints.setAction(action);
        loyaltyPoints.setDescription(description);
        
        if (eventId != null) {
            Event event = eventRepository.findById(eventId).orElse(null);
            loyaltyPoints.setEvent(event);
        }
        
        loyaltyPoints.setEarnedAt(LocalDateTime.now());
        loyaltyPoints.setExpiresAt(LocalDateTime.now().plusYears(1)); // Points expire in 1 year
        
        pointsRepository.save(loyaltyPoints);

        // Update user tier
        updateUserTier(userId);
    }

    @Transactional
    public void updateUserTier(Long userId) {
        UserTier tier = tierRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserTier newTier = new UserTier();
                    User user = userRepository.findById(userId).orElseThrow();
                    newTier.setUser(user);
                    newTier.setTier("BRONZE");
                    newTier.setTotalPoints(0);
                    newTier.setLifetimePoints(0);
                    return newTier;
                });

        Integer totalPoints = pointsRepository.getTotalActivePoints(userId, LocalDateTime.now());
        Integer lifetimePoints = pointsRepository.getLifetimePoints(userId);

        tier.setTotalPoints(totalPoints != null ? totalPoints : 0);
        tier.setLifetimePoints(lifetimePoints != null ? lifetimePoints : 0);

        // Update tier based on lifetime points
        String newTier = calculateTier(tier.getLifetimePoints());
        if (!newTier.equals(tier.getTier())) {
            tier.setTier(newTier);
            tier.setTierAchievedAt(LocalDateTime.now());
        }

        tierRepository.save(tier);
    }

    @Transactional
    public void updateStreak(Long userId, LocalDateTime eventDate) {
        UserTier tier = tierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User tier not found"));

        LocalDateTime lastEvent = tier.getLastEventAttended();
        
        if (lastEvent != null) {
            long daysBetween = ChronoUnit.DAYS.between(lastEvent.toLocalDate(), eventDate.toLocalDate());
            
            if (daysBetween == 1) {
                // Consecutive day - increment streak
                tier.setCurrentStreak(tier.getCurrentStreak() + 1);
                
                // Award streak bonus points
                awardPoints(userId, POINTS_PER_STREAK_DAY, "STREAK", 
                    "Streak bonus: " + tier.getCurrentStreak() + " days", null);
            } else if (daysBetween > 1) {
                // Streak broken
                tier.setCurrentStreak(1);
            }
        } else {
            tier.setCurrentStreak(1);
        }

        // Update longest streak
        if (tier.getCurrentStreak() > tier.getLongestStreak()) {
            tier.setLongestStreak(tier.getCurrentStreak());
        }

        tier.setLastEventAttended(eventDate);
        tierRepository.save(tier);
    }

    public UserTierDTO getUserTier(Long userId) {
        UserTier tier = tierRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Create default tier if not exists
                    UserTier newTier = new UserTier();
                    User user = userRepository.findById(userId).orElseThrow();
                    newTier.setUser(user);
                    newTier.setTier("BRONZE");
                    newTier.setTotalPoints(0);
                    newTier.setLifetimePoints(0);
                    newTier.setCurrentStreak(0);
                    newTier.setLongestStreak(0);
                    return tierRepository.save(newTier);
                });

        UserTierDTO dto = new UserTierDTO();
        dto.setTier(tier.getTier());
        dto.setTotalPoints(tier.getTotalPoints());
        dto.setLifetimePoints(tier.getLifetimePoints());
        dto.setCurrentStreak(tier.getCurrentStreak());
        dto.setLongestStreak(tier.getLongestStreak());
        dto.setLastEventAttended(tier.getLastEventAttended());

        // Calculate points to next tier
        int pointsToNext = calculatePointsToNextTier(tier.getLifetimePoints(), tier.getTier());
        dto.setPointsToNextTier(pointsToNext);
        dto.setNextTier(getNextTier(tier.getTier()));

        return dto;
    }

    public List<LoyaltyPointsDTO> getUserPointsHistory(Long userId) {
        return pointsRepository.findByUserIdOrderByEarnedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void redeemPoints(Long userId, Integer points) {
        UserTier tier = tierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User tier not found"));

        if (tier.getTotalPoints() < points) {
            throw new RuntimeException("Insufficient points");
        }

        // Deduct points
        awardPoints(userId, -points, "REDEMPTION", "Points redeemed", null);
        updateUserTier(userId);
    }

    private String calculateTier(Integer lifetimePoints) {
        if (lifetimePoints >= VIP_THRESHOLD) return "VIP";
        if (lifetimePoints >= GOLD_THRESHOLD) return "GOLD";
        if (lifetimePoints >= SILVER_THRESHOLD) return "SILVER";
        return "BRONZE";
    }

    private int calculatePointsToNextTier(Integer lifetimePoints, String currentTier) {
        switch (currentTier) {
            case "BRONZE": return SILVER_THRESHOLD - lifetimePoints;
            case "SILVER": return GOLD_THRESHOLD - lifetimePoints;
            case "GOLD": return VIP_THRESHOLD - lifetimePoints;
            case "VIP": return 0;
            default: return SILVER_THRESHOLD;
        }
    }

    private String getNextTier(String currentTier) {
        switch (currentTier) {
            case "BRONZE": return "SILVER";
            case "SILVER": return "GOLD";
            case "GOLD": return "VIP";
            case "VIP": return "VIP";
            default: return "SILVER";
        }
    }

    private LoyaltyPointsDTO convertToDTO(LoyaltyPoints points) {
        LoyaltyPointsDTO dto = new LoyaltyPointsDTO();
        dto.setId(points.getId());
        dto.setPoints(points.getPoints());
        dto.setAction(points.getAction());
        dto.setDescription(points.getDescription());
        dto.setEarnedAt(points.getEarnedAt());
        dto.setExpiresAt(points.getExpiresAt());
        
        if (points.getEvent() != null) {
            dto.setEventId(points.getEvent().getId());
            dto.setEventName(points.getEvent().getName());
        }
        
        return dto;
    }
}
