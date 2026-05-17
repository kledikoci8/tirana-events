package com.tirana.events.repository;

import com.tirana.events.model.Curator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuratorRepository extends JpaRepository<Curator, Long> {
    Optional<Curator> findByUserId(Long userId);
    
    @Query("SELECT c FROM Curator c WHERE c.isVerified = true ORDER BY c.followersCount DESC")
    List<Curator> findVerifiedCuratorsOrderByFollowers();
    
    List<Curator> findByIsVerifiedTrueOrderByTotalTicketsSoldDesc();
}
