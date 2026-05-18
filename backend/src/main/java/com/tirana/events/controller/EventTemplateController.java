package com.tirana.events.controller;

import com.tirana.events.dto.*;
import com.tirana.events.model.Event;
import com.tirana.events.service.EventTemplateService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class EventTemplateController {
    private final CurrentUserService currentUserService;
    private final EventTemplateService templateService;

    @GetMapping("/my-templates")
    public ResponseEntity<List<EventTemplateDTO>> getMyTemplates(Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(templateService.getOrganizerTemplates(userId));
    }

    @PostMapping("/from-event/{eventId}")
    public ResponseEntity<EventTemplateDTO> createFromEvent(
            @PathVariable Long eventId,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        // Simplified - would need to fetch event first
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{templateId}/create-event")
    public ResponseEntity<Event> createEventFromTemplate(
            @PathVariable Long templateId,
            @RequestBody CreateEventFromTemplateRequest request) {
        return ResponseEntity.ok(templateService.createEventFromTemplate(templateId, request));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long templateId,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        templateService.deleteTemplate(templateId, userId);
        return ResponseEntity.ok().build();
    }
}
