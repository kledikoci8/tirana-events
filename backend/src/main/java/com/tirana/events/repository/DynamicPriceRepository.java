package com.tirana.events.repository;

import com.tirana.events.model.DynamicPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicPriceRepository extends JpaRepository<DynamicPrice, Long> {
    List<DynamicPrice> findByEventIdOrderByCreatedAtDesc(Long eventId);
    
    @Query("SELECT dp FROM DynamicPrice dp WHERE dp.event.id = :eventId AND dp.isActive = true AND " +
           "(dp.validFrom IS NULL OR dp.validFrom <= :now) AND " +
           "(dp.validUntil IS NULL OR dp.validUntil >= :now) " +
           "ORDER BY dp.price ASC")
    Optional<DynamicPrice> findCurrentPriceForEvent(Long eventId, LocalDateTime now);
    
    List<DynamicPrice> findByEventIdAndPriceType(Long eventId, String priceType);
}
