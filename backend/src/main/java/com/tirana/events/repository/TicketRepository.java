package com.tirana.events.repository;

import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    Optional<Ticket> findByQrCode(String qrCode);
    List<Ticket> findByUserAndStatus(User user, Ticket.TicketStatus status);
}
