package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationHistoryDTO {
    private Long id;
    private String type;
    private String title;
    private String body;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private Long eventId;
    private String eventName;
}
