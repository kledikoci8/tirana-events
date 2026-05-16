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
    
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.ACTIVE;
    
    public enum TicketStatus {
        ACTIVE, USED, CANCELLED
    }
}
