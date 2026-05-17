package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserBadgeDTO {
    private Long id;
    private String badgeCode;
    private String badgeName;
    private String description;
    private String iconUrl;
    private LocalDateTime earnedAt;
    private Boolean isDisplayed;
    private Boolean isNew; // Just earned
}
