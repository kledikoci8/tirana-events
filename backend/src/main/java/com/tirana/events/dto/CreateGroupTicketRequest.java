package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateGroupTicketRequest {
    private Long eventId;
    private Integer totalTickets;
    private List<Long> participantUserIds;
}
