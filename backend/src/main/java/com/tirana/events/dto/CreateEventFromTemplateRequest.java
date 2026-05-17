package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateEventFromTemplateRequest {
    private Long templateId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String imageUrl;
}
