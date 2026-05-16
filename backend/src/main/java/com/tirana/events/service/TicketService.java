package com.tirana.events.service;

import com.tirana.events.model.Event;
import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.TicketRepository;
import com.tirana.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Ticket purchaseTicket(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getMaxAttendees() != null && 
            event.getTickets().size() >= event.getMaxAttendees()) {
            throw new RuntimeException("Event is full");
        }
        
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setQrCode(UUID.randomUUID().toString());
        
        return ticketRepository.save(ticket);
    }
    
    public List<Ticket> getUserTickets(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByUser(user);
    }
}
