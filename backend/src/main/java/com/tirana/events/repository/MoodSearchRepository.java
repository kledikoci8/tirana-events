package com.tirana.events.repository;

import com.tirana.events.model.MoodSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoodSearchRepository extends JpaRepository<MoodSearch, Long> {
    List<MoodSearch> findByUserIdOrderBySearchedAtDesc(Long userId);
    
    List<MoodSearch> findTop10ByOrderBySearchedAtDesc();
}
