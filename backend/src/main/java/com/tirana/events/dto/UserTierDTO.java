package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserTierDTO {
    private String tier;
    private Integer totalPoints;
    private Integer lifetimePoints;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDateTime lastEventAttended;
    private Integer pointsToNextTier;
    private String nextTier;
}
