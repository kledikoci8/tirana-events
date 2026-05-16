package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupParticipantDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Double amountOwed;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private Long ticketId;
}
