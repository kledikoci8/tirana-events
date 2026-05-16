package com.tirana.events.repository;

import com.tirana.events.model.UserPreference;
import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    
    Optional<UserPreference> findByUser(User user);
    
    boolean existsByUser(User user);
}
