package com.tirana.events.repository;

import com.tirana.events.model.EventCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventCheckInRepository extends JpaRepository<EventCheckIn, Long> {
    Optional<EventCheckIn> findByTicketId(Long ticketId);
    
    List<EventCheckIn> findByUserIdOrderByCheckedInAtDesc(Long userId);
    
    List<EventCheckIn> findByEventIdOrderByCheckedInAtDesc(Long eventId);
    
    Boolean existsByTicketId(Long ticketId);
    
    Long countByEventId(Long eventId);
    
    Long countByUserId(Long userId);
}
