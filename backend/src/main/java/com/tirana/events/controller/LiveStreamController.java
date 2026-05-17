package com.tirana.events.controller;

import com.tirana.events.dto.LiveStreamDTO;
import com.tirana.events.service.LiveStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live-streams")
@RequiredArgsConstructor
public class LiveStreamController {
    private final LiveStreamService streamService;

    @GetMapping("/active")
    public ResponseEntity<List<LiveStreamDTO>> getActiveLiveStreams() {
        return ResponseEntity.ok(streamService.getActiveLiveStreams());
    }

    @PostMapping("/events/{eventId}/start")
    public ResponseEntity<LiveStreamDTO> startStream(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(streamService.startStream(eventId, body.get("streamUrl")));
    }

    @PostMapping("/{streamId}/end")
    public ResponseEntity<Void> endStream(@PathVariable Long streamId) {
        streamService.endStream(streamId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{streamId}/view")
    public ResponseEntity<Void> incrementViewers(@PathVariable Long streamId) {
        streamService.incrementViewers(streamId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{streamId}/react")
    public ResponseEntity<Void> addReaction(@PathVariable Long streamId) {
        streamService.addReaction(streamId);
        return ResponseEntity.ok().build();
    }
}
