package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventMemoryDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long eventId;
    private String photoUrl;
    private String caption;
    private LocalDateTime uploadedAt;
    private Integer likes;
    private Boolean isLikedByCurrentUser;
}
