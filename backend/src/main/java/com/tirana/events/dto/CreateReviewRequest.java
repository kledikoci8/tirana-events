package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateReviewRequest {
    private Long eventId;
    private Integer rating; // 1-5
    private String comment;
    private List<String> vibeTags;
}
