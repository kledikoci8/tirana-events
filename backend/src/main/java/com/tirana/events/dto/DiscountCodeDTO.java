package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiscountCodeDTO {
    private Long id;
    private String code;
    private String discountType;
    private Double discountValue;
    private Integer maxUses;
    private Integer currentUses;
    private Integer remainingUses;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private Boolean isValid;
}
