package com.tirana.events.controller;

import com.tirana.events.dto.TicketDTO;
import com.tirana.events.dto.VerifyTicketRequest;
import com.tirana.events.dto.TransferTicketRequest;
import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import com.tirana.events.repository.TicketRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.service.TicketService;
import com.tirana.events.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// FIX B1: Removed @CrossOrigin - CORS is handled globally in SecurityConfig
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/purchase/{eventId}")
    public ResponseEntity<TicketDTO> purchaseTicket(@PathVariable Long eventId,
                                                     Authentication authentication) {
        return ResponseEntity.ok(ticketService.purchaseTicket(eventId, authentication.getName()));
    }
    
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketDTO>> getMyTickets(Authentication authentication) {
        return ResponseEntity.ok(ticketService.getUserTickets(authentication.getName()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicket(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // FIX A3: Check ownership BEFORE any processing to prevent IDOR
        // Return same error as not found to prevent ticket ID enumeration
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Ticket not found");
        }
        
        return ResponseEntity.ok(ticketService.toDto(ticket));
    }
    
    @PostMapping("/{id}/download")
    public ResponseEntity<Void> markAsDownloaded(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // FIX A3: Check ownership BEFORE any processing to prevent IDOR
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Ticket not found");
        }
        
        ticket.setIsDownloaded(true);
        ticket.setDownloadedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/wallet/apple")
    public ResponseEntity<Map<String, Object>> getAppleWalletPass(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // FIX A3: Check ownership BEFORE any processing to prevent IDOR
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Ticket not found");
        }
        
        Map<String, Object> pass = walletService.generateAppleWalletPass(ticket);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(pass);
    }
    
    @GetMapping("/{id}/wallet/google")
    public ResponseEntity<Map<String, Object>> getGoogleWalletPass(
            Authentication authentication,
            @PathVariable Long id) {
        
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // FIX A3: Check ownership BEFORE any processing to prevent IDOR
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Ticket not found");
        }
        
        Map<String, Object> pass = walletService.generateGoogleWalletPass(ticket);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(pass);
    }
    
    @PostMapping("/{id}/verify")
    public ResponseEntity<Map<String, Object>> verifyTicket(
            @PathVariable Long id,
            @RequestBody VerifyTicketRequest request) {
        
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        boolean isValid = false;
        String message = "";
        
        if (ticket.getStatus() != Ticket.TicketStatus.ACTIVE) {
            message = "Ticket is not active";
        } else if (ticket.getCheckedInAt() != null) {
            message = "Ticket already used";
        } else if (request.getNfcData() != null) {
            isValid = walletService.verifyNFCData(request.getNfcData(), ticket);
            message = isValid ? "Valid NFC ticket" : "Invalid NFC data";
        } else if (request.getQrCode() != null) {
            isValid = ticket.getQrCode().equals(request.getQrCode());
            message = isValid ? "Valid QR ticket" : "Invalid QR code";
        }
        
        if (isValid) {
            ticket.setCheckedInAt(LocalDateTime.now());
            ticket.setCheckedInBy(request.getScannerId());
            ticket.setStatus(Ticket.TicketStatus.USED);
            ticketRepository.save(ticket);
        }
        
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "message", message,
            "ticketId", ticket.getId(),
            "eventName", ticket.getEvent().getName(),
            "attendeeName", ticket.getUser().getFullName()
        ));
    }
    
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transferTicket(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody TransferTicketRequest request) {
        
        User user = getCurrentUser(authentication);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // FIX A3: Check ownership BEFORE any processing to prevent IDOR
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Ticket not found");
        }
        
        User recipient = userRepository.findById(request.getRecipientId())
            .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        ticket.setTransferredTo(recipient);
        ticket.setTransferredAt(LocalDateTime.now());
        ticket.setStatus(Ticket.TicketStatus.TRANSFERRED);
        ticket.setUser(recipient);
        
        ticketRepository.save(ticket);
        
        return ResponseEntity.ok().build();
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
}
