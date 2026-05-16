package com.tirana.events.repository;

import com.tirana.events.model.DeviceToken;
import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    
    List<DeviceToken> findByUserAndIsActiveTrue(User user);
    
    Optional<DeviceToken> findByToken(String token);
    
    boolean existsByToken(String token);
}
