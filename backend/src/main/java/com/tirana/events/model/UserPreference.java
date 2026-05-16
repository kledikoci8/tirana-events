package com.tirana.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    // Cold-start onboarding completed
    @Column(nullable = false)
    private Boolean onboardingCompleted = false;
    
    // Selected interests during onboarding
    @ManyToMany
    @JoinTable(
        name = "user_preference_categories",
        joinColumns = @JoinColumn(name = "preference_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> selectedCategories = new HashSet<>();
    
    // Notification preferences
    private Boolean notifyEventReminder = true;
    private Boolean notifyPriceDrop = true;
    private Boolean notifyFriendActivity = true;
    private Boolean notifyNearbyEvents = true;
    
    // Quiet hours
    private Integer quietHoursStart = 23; // 23:00
    private Integer quietHoursEnd = 8;    // 08:00
    
    // Location preferences
    private Double homeLatitude;
    private Double homeLongitude;
    private String homeAddress;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
