package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommunityPostDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long eventId;
    private String eventName;
    private String boardType;
    private String title;
    private String content;
    private String imageUrl;
    private Integer upvotes;
    private Integer commentsCount;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private Boolean isUpvotedByCurrentUser;
}
