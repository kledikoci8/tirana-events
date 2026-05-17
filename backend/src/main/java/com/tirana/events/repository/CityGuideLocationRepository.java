package com.tirana.events.repository;

import com.tirana.events.model.CityGuideLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityGuideLocationRepository extends JpaRepository<CityGuideLocation, Long> {
    List<CityGuideLocation> findByTypeAndIsActiveTrueOrderByRatingDesc(String type);
    
    @Query("SELECT l FROM CityGuideLocation l WHERE l.isActive = true AND " +
           "l.type = :type AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) * " +
           "cos(radians(l.longitude) - radians(:lon)) + sin(radians(:lat)) * " +
           "sin(radians(l.latitude)))) <= :radiusKm " +
           "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) * " +
           "cos(radians(l.longitude) - radians(:lon)) + sin(radians(:lat)) * " +
           "sin(radians(l.latitude)))) ASC")
    List<CityGuideLocation> findNearbyByType(Double lat, Double lon, Double radiusKm, String type);
}
