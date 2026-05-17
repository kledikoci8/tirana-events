package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LiveStreamDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private String eventImageUrl;
    private String streamUrl;
    private String thumbnailUrl;
    private Boolean isLive;
    private Integer viewersCount;
    private Integer reactionsCount;
    private LocalDateTime startedAt;
}
