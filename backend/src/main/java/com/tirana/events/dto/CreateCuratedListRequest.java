package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateCuratedListRequest {
    private String title;
    private String description;
    private String coverImageUrl;
    private List<Long> eventIds;
}
