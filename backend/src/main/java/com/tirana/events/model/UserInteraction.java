package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
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
    private InteractionType type;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // Weight for recommendation algorithm
    private Integer weight;
    
    public enum InteractionType {
        VIEW(1),           // User viewed event details
        SAVE(3),           // User saved/bookmarked event
        PURCHASE(5),       // User purchased ticket
        SHARE(2),          // User shared event
        CLICK(1);          // User clicked on event card
        
        private final int defaultWeight;
        
        InteractionType(int defaultWeight) {
            this.defaultWeight = defaultWeight;
        }
        
        public int getDefaultWeight() {
            return defaultWeight;
        }
    }
    
    @PrePersist
    public void setDefaultWeight() {
        if (weight == null) {
            weight = type.getDefaultWeight();
        }
    }
}
