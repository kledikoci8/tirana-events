package com.tirana.events.repository;

import com.tirana.events.model.EventMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMemoryRepository extends JpaRepository<EventMemory, Long> {
    @Query("SELECT m FROM EventMemory m WHERE m.event.id = :eventId AND m.isApproved = true ORDER BY m.likes DESC, m.uploadedAt DESC")
    List<EventMemory> findApprovedMemoriesByEventId(Long eventId);
    
    List<EventMemory> findByUserIdOrderByUploadedAtDesc(Long userId);
    
    @Query("SELECT m FROM EventMemory m WHERE m.isApproved = false ORDER BY m.uploadedAt ASC")
    List<EventMemory> findPendingApproval();
    
    Long countByEventIdAndIsApprovedTrue(Long eventId);
}
