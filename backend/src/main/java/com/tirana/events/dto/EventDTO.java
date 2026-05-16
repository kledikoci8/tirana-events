package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Long organizerId;
    private String organizerName;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private boolean isSaved;
}
