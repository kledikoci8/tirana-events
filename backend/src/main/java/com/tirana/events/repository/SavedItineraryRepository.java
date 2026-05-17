package com.tirana.events.repository;

import com.tirana.events.model.SavedItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedItineraryRepository extends JpaRepository<SavedItinerary, Long> {
    List<SavedItinerary> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    Optional<SavedItinerary> findByShareToken(String shareToken);
}
