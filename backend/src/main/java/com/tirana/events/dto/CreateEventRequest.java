package com.tirana.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Event name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private Double latitude;
    private Double longitude;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String imageUrl;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private Integer maxAttendees;
}
