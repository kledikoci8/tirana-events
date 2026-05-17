package com.tirana.events.repository;

import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    Optional<Ticket> findByQrCode(String qrCode);
    List<Ticket> findByUserAndStatus(User user, Ticket.TicketStatus status);
    List<Ticket> findByEvent(Event event);
    boolean existsByUserAndEvent(User user, Event event);
    Long countByEvent(Event event);
    
    // Additional methods for services
    List<Ticket> findByUserId(Long userId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    Long countByUserId(Long userId);
}
