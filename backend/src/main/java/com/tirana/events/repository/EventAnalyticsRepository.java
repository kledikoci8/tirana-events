package com.tirana.events.repository;

import com.tirana.events.model.EventAnalytics;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventAnalyticsRepository extends JpaRepository<EventAnalytics, Long> {
    
    Optional<EventAnalytics> findByEvent(Event event);
    
    boolean existsByEvent(Event event);
}
