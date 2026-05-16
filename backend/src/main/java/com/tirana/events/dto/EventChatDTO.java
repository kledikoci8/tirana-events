package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventChatDTO {
    private Long id;
    private UserDTO user;
    private String message;
    private LocalDateTime timestamp;
    private Long replyToId;
}
