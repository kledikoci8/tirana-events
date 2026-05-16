package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public enum ActivityType {
        PURCHASED_TICKET,
        SAVED_EVENT,
        ATTENDING,
        INTERESTED
    }
}
