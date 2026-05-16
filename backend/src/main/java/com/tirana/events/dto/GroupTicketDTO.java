package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupTicketDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private String eventImageUrl;
    private Long organizerId;
    private String organizerName;
    private Integer totalTickets;
    private Double totalPrice;
    private Double pricePerPerson;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private List<GroupParticipantDTO> participants;
    private Integer paidCount;
    private Integer pendingCount;
}
