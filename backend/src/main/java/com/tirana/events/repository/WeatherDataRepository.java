package com.tirana.events.repository;

import com.tirana.events.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    @Query("SELECT w FROM WeatherData w WHERE w.forecastDate >= :startDate AND w.forecastDate <= :endDate ORDER BY w.forecastDate ASC")
    List<WeatherData> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<WeatherData> findFirstByForecastDateOrderByFetchedAtDesc(LocalDateTime forecastDate);
}
