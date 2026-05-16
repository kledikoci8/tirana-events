package com.tirana.events.dto;

import lombok.Data;

@Data
public class CreateInviteRequest {
    private Long eventId;
    private Long inviteeId; // null if inviting non-app user
    private String email;
    private String phone;
}
