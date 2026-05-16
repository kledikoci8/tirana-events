package com.tirana.events.repository;

import com.tirana.events.model.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    List<EventReview> findByEventIdOrderByCreatedAtDesc(Long eventId);
    
    List<EventReview> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT AVG(r.rating) FROM EventReview r WHERE r.event.id = :eventId")
    Double getAverageRating(Long eventId);
    
    Long countByEventId(Long eventId);
    
    Boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    @Query("SELECT r FROM EventReview r WHERE r.event.id = :eventId AND r.isVerifiedAttendee = true ORDER BY r.createdAt DESC")
    List<EventReview> findVerifiedReviewsByEventId(Long eventId);
}
