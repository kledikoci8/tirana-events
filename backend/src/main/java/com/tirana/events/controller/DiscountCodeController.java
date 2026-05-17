package com.tirana.events.controller;

import com.tirana.events.dto.CreateDiscountCodeRequest;
import com.tirana.events.dto.DiscountCodeDTO;
import com.tirana.events.service.DiscountCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discount-codes")
@RequiredArgsConstructor
public class DiscountCodeController {
    private final DiscountCodeService codeService;

    @PostMapping
    public ResponseEntity<DiscountCodeDTO> createCode(@RequestBody CreateDiscountCodeRequest request) {
        return ResponseEntity.ok(codeService.createDiscountCode(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<DiscountCodeDTO> validateCode(
            @RequestParam String code,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(codeService.validateCode(code, eventId));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyCode(@RequestBody Map<String, Object> body) {
        String code = body.get("code").toString();
        Long eventId = Long.parseLong(body.get("eventId").toString());
        codeService.applyCode(code, eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<DiscountCodeDTO>> getEventCodes(@PathVariable Long eventId) {
        return ResponseEntity.ok(codeService.getEventDiscountCodes(eventId));
    }

    @DeleteMapping("/{codeId}")
    public ResponseEntity<Void> deactivateCode(@PathVariable Long codeId) {
        codeService.deactivateCode(codeId);
        return ResponseEntity.ok().build();
    }
}
