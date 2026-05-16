package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckInDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private LocalDateTime checkedInAt;
    private String badgeImageUrl;
    private Integer pointsEarned;
}
