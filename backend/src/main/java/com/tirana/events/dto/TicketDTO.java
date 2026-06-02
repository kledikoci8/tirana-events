package com.tirana.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private String qrCode;
    
    // FIX C2: Use @JsonProperty to map purchaseDate to "purchasedAt" in JSON
    @JsonProperty("purchasedAt")
    private LocalDateTime purchaseDate;
    
    private String status;
    private Boolean isDownloaded;
    private LocalDateTime downloadedAt;
    private Boolean nfcEnabled;
    private LocalDateTime checkedInAt;
    private Double price;
    
    // Event details
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private String eventLocation;
    private String eventImageUrl;
}
