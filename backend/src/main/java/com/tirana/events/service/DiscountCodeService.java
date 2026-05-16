package com.tirana.events.service;

import com.tirana.events.dto.CreateDiscountCodeRequest;
import com.tirana.events.dto.DiscountCodeDTO;
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
public class DiscountCodeService {
    private final DiscountCodeRepository codeRepository;
    private final EventRepository eventRepository;

    @Transactional
    public DiscountCodeDTO createDiscountCode(CreateDiscountCodeRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if code already exists
        if (codeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Discount code already exists");
        }

        DiscountCode code = new DiscountCode();
        code.setEvent(event);
        code.setCode(request.getCode().toUpperCase());
        code.setDiscountType(request.getDiscountType());
        code.setDiscountValue(request.getDiscountValue());
        code.setMaxUses(request.getMaxUses());
        code.setCurrentUses(0);
        code.setValidFrom(request.getValidFrom());
        code.setValidUntil(request.getValidUntil());
        code.setIsActive(true);

        code = codeRepository.save(code);

        return convertToDTO(code);
    }

    public DiscountCodeDTO validateCode(String code, Long eventId) {
        DiscountCode discountCode = codeRepository.findByCodeAndEventId(code.toUpperCase(), eventId)
                .orElseThrow(() -> new RuntimeException("Invalid discount code"));

        LocalDateTime now = LocalDateTime.now();

        // Check if active
        if (!discountCode.getIsActive()) {
            throw new RuntimeException("Discount code is no longer active");
        }

        // Check validity period
        if (discountCode.getValidFrom() != null && now.isBefore(discountCode.getValidFrom())) {
            throw new RuntimeException("Discount code is not yet valid");
        }

        if (discountCode.getValidUntil() != null && now.isAfter(discountCode.getValidUntil())) {
            throw new RuntimeException("Discount code has expired");
        }

        // Check usage limit
        if (discountCode.getMaxUses() != null && 
            discountCode.getCurrentUses() >= discountCode.getMaxUses()) {
            throw new RuntimeException("Discount code has reached maximum uses");
        }

        return convertToDTO(discountCode);
    }

    @Transactional
    public void applyCode(String code, Long eventId) {
        DiscountCode discountCode = codeRepository.findByCodeAndEventId(code.toUpperCase(), eventId)
                .orElseThrow(() -> new RuntimeException("Invalid discount code"));

        discountCode.setCurrentUses(discountCode.getCurrentUses() + 1);
        codeRepository.save(discountCode);
    }

    public List<DiscountCodeDTO> getEventDiscountCodes(Long eventId) {
        return codeRepository.findByEventIdAndIsActiveTrue(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateCode(Long codeId) {
        DiscountCode code = codeRepository.findById(codeId)
                .orElseThrow(() -> new RuntimeException("Discount code not found"));
        code.setIsActive(false);
        codeRepository.save(code);
    }

    public Double calculateDiscountedPrice(Double originalPrice, DiscountCodeDTO code) {
        if (code.getDiscountType().equals("PERCENTAGE")) {
            return originalPrice * (1 - code.getDiscountValue() / 100);
        } else {
            return Math.max(0, originalPrice - code.getDiscountValue());
        }
    }

    private DiscountCodeDTO convertToDTO(DiscountCode code) {
        DiscountCodeDTO dto = new DiscountCodeDTO();
        dto.setId(code.getId());
        dto.setCode(code.getCode());
        dto.setDiscountType(code.getDiscountType());
        dto.setDiscountValue(code.getDiscountValue());
        dto.setMaxUses(code.getMaxUses());
        dto.setCurrentUses(code.getCurrentUses());
        
        if (code.getMaxUses() != null) {
            dto.setRemainingUses(code.getMaxUses() - code.getCurrentUses());
        }
        
        dto.setValidFrom(code.getValidFrom());
        dto.setValidUntil(code.getValidUntil());
        dto.setIsActive(code.getIsActive());
        dto.setIsValid(isCodeValid(code));
        
        return dto;
    }

    private Boolean isCodeValid(DiscountCode code) {
        if (!code.getIsActive()) return false;
        
        LocalDateTime now = LocalDateTime.now();
        if (code.getValidFrom() != null && now.isBefore(code.getValidFrom())) return false;
        if (code.getValidUntil() != null && now.isAfter(code.getValidUntil())) return false;
        if (code.getMaxUses() != null && code.getCurrentUses() >= code.getMaxUses()) return false;
        
        return true;
    }
}
