package com.tirana.events.repository;

import com.tirana.events.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCodeAndEventId(String code, Long eventId);
    
    List<DiscountCode> findByEventIdAndIsActiveTrue(Long eventId);
    
    @Query("SELECT d FROM DiscountCode d WHERE d.event.id = :eventId AND d.isActive = true AND " +
           "(d.validFrom IS NULL OR d.validFrom <= :now) AND " +
           "(d.validUntil IS NULL OR d.validUntil >= :now) AND " +
           "(d.maxUses IS NULL OR d.currentUses < d.maxUses)")
    List<DiscountCode> findActiveCodesForEvent(Long eventId, LocalDateTime now);
    
    Boolean existsByCode(String code);
}
