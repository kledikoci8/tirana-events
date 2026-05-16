package com.tirana.events.repository;

import com.tirana.events.model.GroupTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupTicketRepository extends JpaRepository<GroupTicket, Long> {
    List<GroupTicket> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);
    
    @Query("SELECT gt FROM GroupTicket gt JOIN gt.participants p WHERE p.user.id = :userId ORDER BY gt.createdAt DESC")
    List<GroupTicket> findByParticipantUserId(Long userId);
    
    @Query("SELECT gt FROM GroupTicket gt WHERE gt.status = 'PENDING' AND gt.expiresAt < :now")
    List<GroupTicket> findExpiredPendingTickets(LocalDateTime now);
}
