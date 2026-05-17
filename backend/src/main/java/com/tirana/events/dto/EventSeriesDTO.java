package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventSeriesDTO {
    private Long id;
    private String name;
    private String description;
    private String coverImageUrl;
    private Long organizerId;
    private String organizerName;
    private List<EventDTO> events;
    private LocalDateTime createdAt;
    private Integer totalEvents;
}
