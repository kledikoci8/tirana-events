package com.tirana.events.repository;

import com.tirana.events.model.EventBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventBundleRepository extends JpaRepository<EventBundle, Long> {
    List<EventBundle> findByEventIdAndIsActiveTrueOrderByDistanceAsc(Long eventId);
    
    List<EventBundle> findByEventIdAndTypeAndIsActiveTrue(Long eventId, String type);
}
