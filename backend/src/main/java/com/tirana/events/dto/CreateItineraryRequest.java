package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateItineraryRequest {
    private String name;
    private String description;
    private List<Long> eventIds;
}
