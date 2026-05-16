package com.tirana.events.repository;

import com.tirana.events.model.GroupTicketParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTicketParticipantRepository extends JpaRepository<GroupTicketParticipant, Long> {
    List<GroupTicketParticipant> findByGroupTicketId(Long groupTicketId);
    
    List<GroupTicketParticipant> findByUserIdOrderByJoinedAtDesc(Long userId);
    
    Long countByGroupTicketIdAndPaymentStatus(Long groupTicketId, String status);
}
