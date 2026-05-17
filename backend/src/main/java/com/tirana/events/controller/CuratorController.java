package com.tirana.events.controller;

import com.tirana.events.dto.*;
import com.tirana.events.service.CuratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curators")
@RequiredArgsConstructor
public class CuratorController {
    private final CuratorService curatorService;

    @GetMapping
    public ResponseEntity<List<CuratorDTO>> getVerifiedCurators() {
        return ResponseEntity.ok(curatorService.getVerifiedCurators());
    }

    @GetMapping("/{curatorId}/lists")
    public ResponseEntity<List<CuratedListDTO>> getCuratorLists(@PathVariable Long curatorId) {
        return ResponseEntity.ok(curatorService.getCuratorLists(curatorId));
    }

    @GetMapping("/lists/trending")
    public ResponseEntity<List<CuratedListDTO>> getTrendingLists() {
        return ResponseEntity.ok(curatorService.getTrendingLists());
    }

    @PostMapping("/lists")
    public ResponseEntity<CuratedListDTO> createList(
            @RequestBody CreateCuratedListRequest request,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        // Get curator by userId (simplified)
        return ResponseEntity.ok(curatorService.createCuratedList(userId, request));
    }

    @PostMapping("/lists/{listId}/publish")
    public ResponseEntity<Void> publishList(@PathVariable Long listId) {
        curatorService.publishList(listId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lists/{listId}/view")
    public ResponseEntity<Void> trackView(@PathVariable Long listId) {
        curatorService.trackListView(listId);
        return ResponseEntity.ok().build();
    }
}
