package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HappeningNowDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private LocalDateTime startTime;
    private Integer minutesUntilStart;
    private String venue;
    private Double price;
    private Double lastMinutePrice;
    private Integer discountPercentage;
    private Integer ticketsRemaining;
    private String capacityStatus; // LOW, MEDIUM, HIGH
    private Double distance;
}
