package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_ticket_participants")
@Data
public class GroupTicketParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_ticket_id", nullable = false)
    private GroupTicket groupTicket;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amountOwed;

    @Column(nullable = false)
    private String paymentStatus; // PENDING, PAID, REFUNDED

    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket; // Generated after payment

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
}
