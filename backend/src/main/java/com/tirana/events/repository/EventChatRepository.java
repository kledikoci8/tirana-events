package com.tirana.events.repository;

import com.tirana.events.model.EventChat;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventChatRepository extends JpaRepository<EventChat, Long> {
    
    List<EventChat> findByEventOrderByTimestampAsc(Event event);
    
    List<EventChat> findByEventAndTimestampAfterOrderByTimestampAsc(Event event, LocalDateTime after);
    
    @Query("SELECT ec FROM EventChat ec " +
           "WHERE ec.event = :event " +
           "ORDER BY ec.timestamp DESC")
    List<EventChat> findRecentChatMessages(@Param("event") Event event);
    
    Long countByEvent(Event event);
}
