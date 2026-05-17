package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class MoodSearchResultDTO {
    private String detectedMood;
    private List<String> keywords;
    private List<EventDTO> events;
    private String interpretation; // "Looking for energetic nightlife events"
}
