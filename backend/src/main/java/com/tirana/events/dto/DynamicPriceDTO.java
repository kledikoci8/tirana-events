package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DynamicPriceDTO {
    private Long id;
    private Double price;
    private String priceType;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer ticketsRemaining;
    private Double originalPrice;
    private Integer discountPercentage;
    private Long hoursRemaining;
}
