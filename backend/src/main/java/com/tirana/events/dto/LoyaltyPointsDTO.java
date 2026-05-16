package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoyaltyPointsDTO {
    private Long id;
    private Integer points;
    private String action;
    private String description;
    private Long eventId;
    private String eventName;
    private LocalDateTime earnedAt;
    private LocalDateTime expiresAt;
}
