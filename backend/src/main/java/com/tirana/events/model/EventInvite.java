package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;
    
    // Can be null if inviting non-app user
    @ManyToOne
    @JoinColumn(name = "invitee_id")
    private User invitee;
    
    // For non-app users
    private String inviteeEmail;
    private String inviteePhone;
    
    @Column(unique = true, nullable = false)
    private String inviteToken;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status = InviteStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime acceptedAt;
    
    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        EXPIRED
    }
}
