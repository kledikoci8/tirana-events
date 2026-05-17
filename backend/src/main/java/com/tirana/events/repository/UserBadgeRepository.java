package com.tirana.events.repository;

import com.tirana.events.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserIdOrderByEarnedAtDesc(Long userId);
    
    Optional<UserBadge> findByUserIdAndBadgeCode(Long userId, String badgeCode);
    
    Boolean existsByUserIdAndBadgeCode(Long userId, String badgeCode);
    
    Long countByUserId(Long userId);
}
