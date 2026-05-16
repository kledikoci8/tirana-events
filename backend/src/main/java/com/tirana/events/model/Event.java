package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private String location;
    
    private Double latitude;
    private Double longitude;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String imageUrl;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;
    
    private Integer maxAttendees;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<Ticket> tickets = new HashSet<>();
    
    @ManyToMany(mappedBy = "savedEvents")
    private Set<User> savedByUsers = new HashSet<>();
    
    // New fields for filtering
    private Double price; // Price in ALL (Albanian Lek)
    private Boolean isFree = false;
    private Integer ticketsAvailable;
    private Boolean isOutdoor = false;
    
    // Accessibility features
    private Boolean wheelchairAccessible = false;
    private Boolean hearingLoopAvailable = false;
    private Boolean seatedVenue = false;
}
