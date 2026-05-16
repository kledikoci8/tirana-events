package com.tirana.events.dto;

import lombok.Data;

@Data
public class SavePresetRequest {
    private String name;
    private EventFilterRequest filter;
}
