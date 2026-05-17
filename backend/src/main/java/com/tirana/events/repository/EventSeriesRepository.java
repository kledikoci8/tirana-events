package com.tirana.events.repository;

import com.tirana.events.model.EventSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSeriesRepository extends JpaRepository<EventSeries, Long> {
    List<EventSeries> findByOrganizerIdAndIsActiveTrueOrderByCreatedAtDesc(Long organizerId);
    
    List<EventSeries> findByIsActiveTrueOrderByCreatedAtDesc();
}
