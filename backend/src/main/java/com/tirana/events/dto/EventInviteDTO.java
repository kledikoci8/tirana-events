package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventInviteDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private UserDTO inviter;
    private UserDTO invitee;
    private String status;
    private LocalDateTime createdAt;
    private String deepLink;
}
