package com.tirana.events.repository;

import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT t FROM Ticket t JOIN FETCH t.event WHERE t.user = :user ORDER BY t.event.startDate DESC")
    List<Ticket> findByUserWithEvent(@Param("user") User user);

    @Query("SELECT DISTINCT t.user FROM Ticket t WHERE t.event = :event AND t.user IN :friends AND t.status = com.tirana.events.model.Ticket.TicketStatus.ACTIVE")
    List<User> findFriendsWithTicketsForEvent(@Param("event") Event event, @Param("friends") List<User> friends);

    @Query("SELECT COUNT(DISTINCT t.user) FROM Ticket t WHERE t.event = :event AND t.user IN :friends AND t.status = com.tirana.events.model.Ticket.TicketStatus.ACTIVE")
    Long countFriendsWithTicketsForEvent(@Param("event") Event event, @Param("friends") List<User> friends);
}
