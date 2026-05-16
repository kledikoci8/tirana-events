package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventReviewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long eventId;
    private Integer rating;
    private String comment;
    private List<String> vibeTags;
    private LocalDateTime createdAt;
    private Boolean isVerifiedAttendee;
    private Integer helpfulCount;
    private String organizerReply;
    private LocalDateTime organizerRepliedAt;
}
