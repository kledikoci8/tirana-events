package com.tirana.events.repository;

import com.tirana.events.model.EventTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTemplateRepository extends JpaRepository<EventTemplate, Long> {
    List<EventTemplate> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);
    
    List<EventTemplate> findByOrganizerIdOrderByTimesUsedDesc(Long organizerId);
}
