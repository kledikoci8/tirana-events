package com.tirana.events.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private String qrCode;
    private LocalDateTime purchaseDate;
    private String status;
    private Boolean isDownloaded;
    private LocalDateTime downloadedAt;
    private Boolean nfcEnabled;
    private LocalDateTime checkedInAt;
    
    // Event details
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private String eventLocation;
    private String eventImageUrl;
}
