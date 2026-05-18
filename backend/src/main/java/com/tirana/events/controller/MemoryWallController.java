package com.tirana.events.controller;

import com.tirana.events.dto.EventMemoryDTO;
import com.tirana.events.service.MemoryWallService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
public class MemoryWallController {
    private final CurrentUserService currentUserService;
    private final MemoryWallService memoryWallService;

    @PostMapping
    public ResponseEntity<EventMemoryDTO> uploadMemory(
            @RequestBody Map<String, Object> body,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        Long eventId = Long.parseLong(body.get("eventId").toString());
        String photoUrl = body.get("photoUrl").toString();
        String caption = body.get("caption").toString();
        
        return ResponseEntity.ok(memoryWallService.uploadMemory(userId, eventId, photoUrl, caption));
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<EventMemoryDTO>> getEventMemories(
            @PathVariable Long eventId,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(memoryWallService.getEventMemories(eventId, userId));
    }

    @GetMapping("/my-memories")
    public ResponseEntity<List<EventMemoryDTO>> getMyMemories(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(memoryWallService.getUserMemories(userId));
    }

    @PostMapping("/{memoryId}/like")
    public ResponseEntity<Void> likeMemory(@PathVariable Long memoryId) {
        memoryWallService.likeMemory(memoryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memoryId}/report")
    public ResponseEntity<Void> reportMemory(@PathVariable Long memoryId) {
        memoryWallService.reportMemory(memoryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memoryId}/approve")
    public ResponseEntity<Void> approveMemory(@PathVariable Long memoryId) {
        memoryWallService.approveMemory(memoryId);
        return ResponseEntity.ok().build();
    }
}
