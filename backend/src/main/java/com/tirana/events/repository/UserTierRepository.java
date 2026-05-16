package com.tirana.events.repository;

import com.tirana.events.model.UserTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTierRepository extends JpaRepository<UserTier, Long> {
    Optional<UserTier> findByUserId(Long userId);
    
    @Query("SELECT ut FROM UserTier ut ORDER BY ut.lifetimePoints DESC")
    List<UserTier> findTopUsersByPoints();
    
    @Query("SELECT ut FROM UserTier ut WHERE ut.tier = :tier ORDER BY ut.lifetimePoints DESC")
    List<UserTier> findByTier(String tier);
}
