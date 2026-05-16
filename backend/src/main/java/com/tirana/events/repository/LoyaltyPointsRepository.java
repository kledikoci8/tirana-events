package com.tirana.events.repository;

import com.tirana.events.model.LoyaltyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, Long> {
    List<LoyaltyPoints> findByUserIdOrderByEarnedAtDesc(Long userId);
    
    @Query("SELECT SUM(lp.points) FROM LoyaltyPoints lp WHERE lp.user.id = :userId AND (lp.expiresAt IS NULL OR lp.expiresAt > :now)")
    Integer getTotalActivePoints(Long userId, LocalDateTime now);
    
    @Query("SELECT SUM(lp.points) FROM LoyaltyPoints lp WHERE lp.user.id = :userId")
    Integer getLifetimePoints(Long userId);
    
    List<LoyaltyPoints> findByUserIdAndExpiresAtBefore(Long userId, LocalDateTime date);
}
