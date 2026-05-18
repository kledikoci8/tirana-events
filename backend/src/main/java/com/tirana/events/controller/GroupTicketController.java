package com.tirana.events.controller;

import com.tirana.events.dto.CreateGroupTicketRequest;
import com.tirana.events.dto.GroupTicketDTO;
import com.tirana.events.service.GroupTicketService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-tickets")
@RequiredArgsConstructor
public class GroupTicketController {
    private final CurrentUserService currentUserService;
    private final GroupTicketService groupTicketService;

    @PostMapping
    public ResponseEntity<GroupTicketDTO> createGroupTicket(
            @RequestBody CreateGroupTicketRequest request,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(groupTicketService.createGroupTicket(userId, request));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<GroupTicketDTO>> getMyGroupTickets(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(groupTicketService.getUserGroupTickets(userId));
    }

    @GetMapping("/{groupTicketId}")
    public ResponseEntity<GroupTicketDTO> getGroupTicket(@PathVariable Long groupTicketId) {
        return ResponseEntity.ok(groupTicketService.getGroupTicket(groupTicketId));
    }

    @PostMapping("/participants/{participantId}/pay")
    public ResponseEntity<Void> payForTicket(
            @PathVariable Long participantId,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        groupTicketService.payForTicket(participantId, userId);
        return ResponseEntity.ok().build();
    }
}
