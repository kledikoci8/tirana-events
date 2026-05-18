package com.tirana.events.service;

import com.tirana.events.dto.TicketDTO;
import com.tirana.events.exception.BadRequestException;
import com.tirana.events.exception.ConflictException;
import com.tirana.events.exception.NotFoundException;
import com.tirana.events.model.Event;
import com.tirana.events.model.FriendActivity;
import com.tirana.events.model.Ticket;
import com.tirana.events.model.User;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.TicketRepository;
import com.tirana.events.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SocialService socialService;
    private final AnalyticsService analyticsService;
    private final LoyaltyService loyaltyService;
    private final NotificationService notificationService;
    private final DynamicPricingService dynamicPricingService;

    @Transactional
    public TicketDTO purchaseTicket(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getOrganizer() != null && event.getOrganizer().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot purchase a ticket to your own event");
        }

        if (ticketRepository.existsByUserAndEvent(user, event)) {
            throw new ConflictException("You already have a ticket for this event");
        }

        long soldCount = ticketRepository.countByEvent(event);
        if (event.getMaxAttendees() != null && soldCount >= event.getMaxAttendees()) {
            throw new ConflictException("Tickets are no longer available");
        }

        analyticsService.trackPurchaseAttempt(event);

        double ticketPrice;
        if (Boolean.TRUE.equals(event.getIsFree()) || event.getPrice() == null) {
            ticketPrice = 0.0;
        } else {
            try {
                ticketPrice = dynamicPricingService.getCurrentPrice(eventId).getPrice();
            } catch (Exception e) {
                ticketPrice = event.getPrice();
            }
        }

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setUser(user);
        ticket.setQrCode(UUID.randomUUID().toString());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setPrice(ticketPrice);
        ticket.setIsDownloaded(true);
        ticket.setDownloadedAt(LocalDateTime.now());

        ticket = ticketRepository.save(ticket);

        if (event.getTicketsAvailable() != null && event.getTicketsAvailable() > 0) {
            event.setTicketsAvailable(event.getTicketsAvailable() - 1);
            eventRepository.save(event);
        }

        socialService.recordActivity(user, event, FriendActivity.ActivityType.PURCHASED_TICKET);
        socialService.notifyFollowersOfPurchase(user, event);
        analyticsService.trackCompletedPurchase(event, ticketPrice);
        loyaltyService.awardPoints(user.getId(), 100, "TICKET_PURCHASE",
                "Ticket purchased for " + event.getName(), eventId);
        loyaltyService.updateStreak(user.getId(), event.getStartDate());
        notificationService.scheduleEventReminders(event, List.of(user));

        return toDto(ticket);
    }

    public List<TicketDTO> getUserTickets(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return ticketRepository.findByUserWithEvent(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TicketDTO toDto(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setQrCode(ticket.getQrCode());
        dto.setPurchaseDate(ticket.getPurchaseDate());
        dto.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : Ticket.TicketStatus.ACTIVE.name());
        dto.setIsDownloaded(ticket.getIsDownloaded());
        dto.setDownloadedAt(ticket.getDownloadedAt());
        dto.setNfcEnabled(ticket.getNfcEnabled());
        dto.setCheckedInAt(ticket.getCheckedInAt());
        dto.setPrice(ticket.getPrice());

        if (ticket.getEvent() != null) {
            Event event = ticket.getEvent();
            dto.setEventId(event.getId());
            dto.setEventName(event.getName());
            dto.setEventDate(event.getStartDate());
            dto.setEventLocation(event.getVenue() != null ? event.getVenue() : event.getLocation());
            dto.setEventImageUrl(event.getImageUrl());
        }

        return dto;
    }
}
