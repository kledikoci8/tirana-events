package com.tirana.events.dto;

import lombok.Data;

@Data
public class SendChatRequest {
    private String message;
    private Long replyToId;
}
