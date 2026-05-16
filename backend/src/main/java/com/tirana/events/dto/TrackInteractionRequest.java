package com.tirana.events.dto;

import com.tirana.events.model.UserInteraction;
import lombok.Data;

@Data
public class TrackInteractionRequest {
    private Long eventId;
    private UserInteraction.InteractionType type;
}
