package com.tirana.events.repository;

import com.tirana.events.model.FilterPreset;
import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterPresetRepository extends JpaRepository<FilterPreset, Long> {
    
    List<FilterPreset> findByUserOrderByCreatedAtDesc(User user);
    
    boolean existsByUserAndName(User user, String name);
}
