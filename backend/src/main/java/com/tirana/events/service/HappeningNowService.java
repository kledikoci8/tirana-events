package com.tirana.events.service;

import com.tirana.events.dto.HappeningNowDTO;
import com.tirana.events.model.Event;
import com.tirana.events.repository.DynamicPriceRepository;
import com.tirana.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HappeningNowService {
    private final EventRepository eventRepository;
    private final DynamicPriceRepository priceRepository;

    public List<HappeningNowDTO> getHappeningNowEvents(Double userLat, Double userLon) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);

        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> e.getStartTime().isAfter(now) && e.getStartTime().isBefore(twoHoursLater))
                .collect(Collectors.toList());

        return events.stream()
                .map(e -> convertToDTO(e, userLat, userLon))
                .collect(Collectors.toList());
    }

    private HappeningNowDTO convertToDTO(Event event, Double userLat, Double userLon) {
        HappeningNowDTO dto = new HappeningNowDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setImageUrl(event.getImageUrl());
        dto.setStartTime(event.getStartTime());
        
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), event.getStartTime());
        dto.setMinutesUntilStart((int) minutes);
        
        dto.setVenue(event.getVenue());
        dto.setPrice(event.getPrice());

        // Check for last-minute pricing
        var lastMinutePrice = priceRepository.findCurrentPriceForEvent(event.getId(), LocalDateTime.now());
        if (lastMinutePrice.isPresent() && lastMinutePrice.get().getPriceType().equals("LAST_MINUTE")) {
            dto.setLastMinutePrice(lastMinutePrice.get().getPrice());
            int discount = (int) ((event.getPrice() - lastMinutePrice.get().getPrice()) / event.getPrice() * 100);
            dto.setDiscountPercentage(discount);
        }

        dto.setTicketsRemaining(event.getTicketsAvailable());

        // Calculate capacity status
        if (event.getTicketsAvailable() != null) {
            if (event.getTicketsAvailable() < 10) {
                dto.setCapacityStatus("LOW");
            } else if (event.getTicketsAvailable() < 50) {
                dto.setCapacityStatus("MEDIUM");
            } else {
                dto.setCapacityStatus("HIGH");
            }
        }

        // Calculate distance
        if (userLat != null && userLon != null && event.getLatitude() != null && event.getLongitude() != null) {
            double distance = calculateDistance(userLat, userLon, event.getLatitude(), event.getLongitude());
            dto.setDistance(distance);
        }

        return dto;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
