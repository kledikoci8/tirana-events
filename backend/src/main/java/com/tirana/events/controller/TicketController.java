package com.tirana.events.controller;

import com.tirana.events.model.Ticket;
import com.tirana.events.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @PostMapping("/purchase/{eventId}")
    public ResponseEntity<Ticket> purchaseTicket(@PathVariable Long eventId,
                                                  Authentication authentication) {
        return ResponseEntity.ok(ticketService.purchaseTicket(eventId, authentication.getName()));
    }
    
    @GetMapping("/my-tickets")
    public ResponseEntity<List<Ticket>> getMyTickets(Authentication authentication) {
        return ResponseEntity.ok(ticketService.getUserTickets(authentication.getName()));
    }
}
