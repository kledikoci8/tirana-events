package com.tirana.events.controller;

import com.tirana.events.dto.CheckInDTO;
import com.tirana.events.service.CheckInService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckInController {
    private final CurrentUserService currentUserService;
    private final CheckInService checkInService;

    @PostMapping("/tickets/{ticketId}")
    public ResponseEntity<CheckInDTO> checkIn(@PathVariable Long ticketId) {
        return ResponseEntity.ok(checkInService.checkIn(ticketId));
    }

    @GetMapping("/my-checkins")
    public ResponseEntity<List<CheckInDTO>> getMyCheckIns(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(checkInService.getUserCheckIns(userId));
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<CheckInDTO> getCheckInByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(checkInService.getCheckInByTicket(ticketId));
    }
}
