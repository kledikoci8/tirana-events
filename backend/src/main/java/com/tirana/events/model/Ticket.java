package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(unique = true, nullable = false)
    private String qrCode;
    
    @Column(nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();
    
    /** Actual amount paid; may differ from event price (early bird, group split, etc.). */
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.ACTIVE;
    
    // Wallet integration
    private String walletPassUrl;
    private Boolean nfcEnabled = false;
    private String nfcData;
    
    // Offline access
    private LocalDateTime downloadedAt;
    private Boolean isDownloaded = false;
    
    // Transfer
    @ManyToOne
    @JoinColumn(name = "transferred_to_id")
    private User transferredTo;
    
    private LocalDateTime transferredAt;
    
    // Check-in
    private LocalDateTime checkedInAt;
    private String checkedInBy; // Staff member or scanner ID
    
    public enum TicketStatus {
        ACTIVE, 
        USED, 
        CANCELLED,
        TRANSFERRED
    }
    
    // Convenience methods for backward compatibility
    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchaseDate = purchasedAt;
    }
    
    public LocalDateTime getPurchasedAt() {
        return this.purchaseDate;
    }
}
