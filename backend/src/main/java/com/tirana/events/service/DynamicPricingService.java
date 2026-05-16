package com.tirana.events.service;

import com.tirana.events.dto.DynamicPriceDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicPricingService {
    private final DynamicPriceRepository priceRepository;
    private final EventRepository eventRepository;

    @Transactional
    public DynamicPriceDTO createDynamicPrice(Long eventId, Double price, String priceType, 
                                              LocalDateTime validFrom, LocalDateTime validUntil) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        DynamicPrice dynamicPrice = new DynamicPrice();
        dynamicPrice.setEvent(event);
        dynamicPrice.setPrice(price);
        dynamicPrice.setPriceType(priceType);
        dynamicPrice.setValidFrom(validFrom);
        dynamicPrice.setValidUntil(validUntil);
        dynamicPrice.setIsActive(true);

        dynamicPrice = priceRepository.save(dynamicPrice);

        return convertToDTO(dynamicPrice, event.getPrice());
    }

    public DynamicPriceDTO getCurrentPrice(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        DynamicPrice currentPrice = priceRepository.findCurrentPriceForEvent(eventId, LocalDateTime.now())
                .orElse(null);

        if (currentPrice != null) {
            return convertToDTO(currentPrice, event.getPrice());
        }

        // Return regular price if no dynamic pricing active
        DynamicPriceDTO dto = new DynamicPriceDTO();
        dto.setPrice(event.getPrice());
        dto.setPriceType("REGULAR");
        dto.setOriginalPrice(event.getPrice());
        dto.setDiscountPercentage(0);
        return dto;
    }

    public List<DynamicPriceDTO> getPriceHistory(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return priceRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(p -> convertToDTO(p, event.getPrice()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createEarlyBirdPricing(Long eventId, Double discountPercentage, LocalDateTime until) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Double earlyBirdPrice = event.getPrice() * (1 - discountPercentage / 100);

        DynamicPrice price = new DynamicPrice();
        price.setEvent(event);
        price.setPrice(earlyBirdPrice);
        price.setPriceType("EARLY_BIRD");
        price.setValidFrom(LocalDateTime.now());
        price.setValidUntil(until);
        price.setIsActive(true);

        priceRepository.save(price);
    }

    @Transactional
    public void createLastMinuteDeal(Long eventId, Double discountPercentage) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Double lastMinutePrice = event.getPrice() * (1 - discountPercentage / 100);

        DynamicPrice price = new DynamicPrice();
        price.setEvent(event);
        price.setPrice(lastMinutePrice);
        price.setPriceType("LAST_MINUTE");
        price.setValidFrom(LocalDateTime.now());
        price.setValidUntil(event.getStartTime());
        price.setIsActive(true);

        priceRepository.save(price);
    }

    private DynamicPriceDTO convertToDTO(DynamicPrice price, Double originalPrice) {
        DynamicPriceDTO dto = new DynamicPriceDTO();
        dto.setId(price.getId());
        dto.setPrice(price.getPrice());
        dto.setPriceType(price.getPriceType());
        dto.setValidFrom(price.getValidFrom());
        dto.setValidUntil(price.getValidUntil());
        dto.setTicketsRemaining(price.getTicketsRemaining());
        dto.setOriginalPrice(originalPrice);

        // Calculate discount percentage
        if (originalPrice != null && originalPrice > 0) {
            int discount = (int) ((originalPrice - price.getPrice()) / originalPrice * 100);
            dto.setDiscountPercentage(discount);
        }

        // Calculate hours remaining
        if (price.getValidUntil() != null) {
            long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), price.getValidUntil());
            dto.setHoursRemaining(hours);
        }

        return dto;
    }
}
