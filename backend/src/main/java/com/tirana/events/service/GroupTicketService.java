package com.tirana.events.service;

import com.tirana.events.dto.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupTicketService {
    private final GroupTicketRepository groupTicketRepository;
    private final GroupTicketParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public GroupTicketDTO createGroupTicket(Long organizerId, CreateGroupTicketRequest request) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        GroupTicket groupTicket = new GroupTicket();
        groupTicket.setEvent(event);
        groupTicket.setOrganizer(organizer);
        groupTicket.setTotalTickets(request.getTotalTickets());
        groupTicket.setTotalPrice(event.getPrice() * request.getTotalTickets());
        groupTicket.setStatus("PENDING");
        groupTicket.setCreatedAt(LocalDateTime.now());
        groupTicket.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24h to complete payment

        groupTicket = groupTicketRepository.save(groupTicket);

        // Add organizer as first participant
        addParticipant(groupTicket, organizer, groupTicket.getTotalPrice() / request.getTotalTickets());

        // Add other participants
        for (Long userId : request.getParticipantUserIds()) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                addParticipant(groupTicket, user, groupTicket.getTotalPrice() / request.getTotalTickets());
            }
        }

        return convertToDTO(groupTicket);
    }

    private void addParticipant(GroupTicket groupTicket, User user, Double amount) {
        GroupTicketParticipant participant = new GroupTicketParticipant();
        participant.setGroupTicket(groupTicket);
        participant.setUser(user);
        participant.setAmountOwed(amount);
        participant.setPaymentStatus("PENDING");
        participant.setJoinedAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    @Transactional
    public void payForTicket(Long participantId, Long userId) {
        GroupTicketParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        if (!participant.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (!participant.getPaymentStatus().equals("PENDING")) {
            throw new RuntimeException("Payment already processed");
        }

        // Process payment (integrate with payment gateway)
        participant.setPaymentStatus("PAID");
        participant.setPaidAt(LocalDateTime.now());

        // Generate individual ticket
        Ticket ticket = new Ticket();
        ticket.setUser(participant.getUser());
        ticket.setEvent(participant.getGroupTicket().getEvent());
        ticket.setPrice(participant.getAmountOwed());
        ticket.setPurchasedAt(LocalDateTime.now());
        ticket.setQrCode("QR-" + System.currentTimeMillis());
        ticket = ticketRepository.save(ticket);

        participant.setTicket(ticket);
        participantRepository.save(participant);

        // Check if all paid
        checkGroupCompletion(participant.getGroupTicket().getId());
    }

    @Transactional
    private void checkGroupCompletion(Long groupTicketId) {
        GroupTicket groupTicket = groupTicketRepository.findById(groupTicketId)
                .orElseThrow(() -> new RuntimeException("Group ticket not found"));

        Long paidCount = participantRepository.countByGroupTicketIdAndPaymentStatus(groupTicketId, "PAID");
        
        if (paidCount >= groupTicket.getTotalTickets()) {
            groupTicket.setStatus("COMPLETED");
            groupTicketRepository.save(groupTicket);
        }
    }

    public List<GroupTicketDTO> getUserGroupTickets(Long userId) {
        List<GroupTicket> organized = groupTicketRepository.findByOrganizerIdOrderByCreatedAtDesc(userId);
        List<GroupTicket> participating = groupTicketRepository.findByParticipantUserId(userId);

        List<GroupTicket> all = new ArrayList<>();
        all.addAll(organized);
        all.addAll(participating);

        return all.stream()
                .distinct()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GroupTicketDTO getGroupTicket(Long groupTicketId) {
        GroupTicket groupTicket = groupTicketRepository.findById(groupTicketId)
                .orElseThrow(() -> new RuntimeException("Group ticket not found"));
        return convertToDTO(groupTicket);
    }

    private GroupTicketDTO convertToDTO(GroupTicket groupTicket) {
        GroupTicketDTO dto = new GroupTicketDTO();
        dto.setId(groupTicket.getId());
        dto.setEventId(groupTicket.getEvent().getId());
        dto.setEventName(groupTicket.getEvent().getName());
        dto.setEventImageUrl(groupTicket.getEvent().getImageUrl());
        dto.setOrganizerId(groupTicket.getOrganizer().getId());
        dto.setOrganizerName(groupTicket.getOrganizer().getName());
        dto.setTotalTickets(groupTicket.getTotalTickets());
        dto.setTotalPrice(groupTicket.getTotalPrice());
        dto.setPricePerPerson(groupTicket.getTotalPrice() / groupTicket.getTotalTickets());
        dto.setStatus(groupTicket.getStatus());
        dto.setCreatedAt(groupTicket.getCreatedAt());
        dto.setExpiresAt(groupTicket.getExpiresAt());

        List<GroupTicketParticipant> participants = participantRepository.findByGroupTicketId(groupTicket.getId());
        dto.setParticipants(participants.stream().map(this::convertParticipantToDTO).collect(Collectors.toList()));
        
        Long paidCount = participantRepository.countByGroupTicketIdAndPaymentStatus(groupTicket.getId(), "PAID");
        dto.setPaidCount(paidCount.intValue());
        dto.setPendingCount(groupTicket.getTotalTickets() - paidCount.intValue());

        return dto;
    }

    private GroupParticipantDTO convertParticipantToDTO(GroupTicketParticipant participant) {
        GroupParticipantDTO dto = new GroupParticipantDTO();
        dto.setId(participant.getId());
        dto.setUserId(participant.getUser().getId());
        dto.setUserName(participant.getUser().getName());
        dto.setUserAvatar(participant.getUser().getProfilePicture());
        dto.setAmountOwed(participant.getAmountOwed());
        dto.setPaymentStatus(participant.getPaymentStatus());
        dto.setPaidAt(participant.getPaidAt());
        if (participant.getTicket() != null) {
            dto.setTicketId(participant.getTicket().getId());
        }
        return dto;
    }
}
