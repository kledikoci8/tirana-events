package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "group_tickets")
@Data
public class GroupTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer; // Person who initiated the group purchase

    @Column(nullable = false)
    private Integer totalTickets;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "groupTicket", cascade = CascadeType.ALL)
    private List<GroupTicketParticipant> participants;
}
