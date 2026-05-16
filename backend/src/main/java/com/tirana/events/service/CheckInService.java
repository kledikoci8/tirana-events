package com.tirana.events.service;

import com.tirana.events.dto.CheckInDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final EventCheckInRepository checkInRepository;
    private final TicketRepository ticketRepository;
    private final LoyaltyService loyaltyService;

    @Transactional
    public CheckInDTO checkIn(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if already checked in
        if (checkInRepository.existsByTicketId(ticketId)) {
            throw new RuntimeException("Ticket already checked in");
        }

        // Verify ticket is valid
        if (ticket.getCheckedInAt() != null) {
            throw new RuntimeException("Ticket already used");
        }

        EventCheckIn checkIn = new EventCheckIn();
        checkIn.setUser(ticket.getUser());
        checkIn.setEvent(ticket.getEvent());
        checkIn.setTicket(ticket);
        checkIn.setCheckedInAt(LocalDateTime.now());

        // Generate proof of attendance badge
        String badgeUrl = generateBadge(ticket.getEvent(), ticket.getUser());
        checkIn.setBadgeImageUrl(badgeUrl);
        checkIn.setBadgeGenerated(true);

        checkIn = checkInRepository.save(checkIn);

        // Update ticket
        ticket.setCheckedInAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        // Award loyalty points
        int points = 25;
        loyaltyService.awardPoints(ticket.getUser().getId(), points, "CHECK_IN", 
            "Checked in to " + ticket.getEvent().getName(), ticket.getEvent().getId());

        // Update streak
        loyaltyService.updateStreak(ticket.getUser().getId(), ticket.getEvent().getStartTime());

        return convertToDTO(checkIn, points);
    }

    public List<CheckInDTO> getUserCheckIns(Long userId) {
        return checkInRepository.findByUserIdOrderByCheckedInAtDesc(userId)
                .stream()
                .map(c -> convertToDTO(c, 0))
                .collect(Collectors.toList());
    }

    public CheckInDTO getCheckInByTicket(Long ticketId) {
        EventCheckIn checkIn = checkInRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Check-in not found"));
        return convertToDTO(checkIn, 0);
    }

    private String generateBadge(Event event, User user) {
        // In production, this would generate an actual badge image
        // For now, return a placeholder URL
        return "https://api.tiranaevents.com/badges/" + event.getId() + "/" + user.getId() + ".png";
    }

    private CheckInDTO convertToDTO(EventCheckIn checkIn, Integer pointsEarned) {
        CheckInDTO dto = new CheckInDTO();
        dto.setId(checkIn.getId());
        dto.setEventId(checkIn.getEvent().getId());
        dto.setEventName(checkIn.getEvent().getName());
        dto.setCheckedInAt(checkIn.getCheckedInAt());
        dto.setBadgeImageUrl(checkIn.getBadgeImageUrl());
        dto.setPointsEarned(pointsEarned);
        return dto;
    }
}
