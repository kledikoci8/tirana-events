package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FriendActivityDTO {
    private Long id;
    private UserDTO user;
    private Long eventId;
    private String eventName;
    private String eventImageUrl;
    private String activityType;
    private LocalDateTime timestamp;
}
