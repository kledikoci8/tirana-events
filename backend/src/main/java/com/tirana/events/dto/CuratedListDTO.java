package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CuratedListDTO {
    private Long id;
    private CuratorDTO curator;
    private String title;
    private String description;
    private String coverImageUrl;
    private List<EventDTO> events;
    private Integer viewsCount;
    private Integer savesCount;
    private Integer ticketsSold;
    private LocalDateTime publishedAt;
}
