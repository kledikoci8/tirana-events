package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateDiscountCodeRequest {
    private Long eventId;
    private String code;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private Double discountValue;
    private Integer maxUses;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}
