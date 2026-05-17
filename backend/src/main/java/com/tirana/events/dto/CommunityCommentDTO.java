package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommunityCommentDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private Integer upvotes;
    private LocalDateTime createdAt;
}
