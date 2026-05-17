package com.tirana.events.dto;

import lombok.Data;

@Data
public class CreateCommunityPostRequest {
    private Long eventId;
    private String boardType;
    private String title;
    private String content;
    private String imageUrl;
}
