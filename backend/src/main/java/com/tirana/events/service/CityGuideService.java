package com.tirana.events.service;

import com.tirana.events.dto.CityGuideLocationDTO;
import com.tirana.events.model.CityGuideLocation;
import com.tirana.events.model.Event;
import com.tirana.events.repository.CityGuideLocationRepository;
import com.tirana.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityGuideService {
    private final CityGuideLocationRepository locationRepository;
    private final EventRepository eventRepository;

    public List<CityGuideLocationDTO> getNearbyLocations(Long eventId, String type) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (event.getLatitude() == null || event.getLongitude() == null) {
            return List.of();
        }
        
        List<CityGuideLocation> locations = locationRepository.findNearbyByType(
            event.getLatitude(), 
            event.getLongitude(), 
            2.0, // 2km radius
            type
        );
        
        return locations.stream()
            .map(l -> convertToDTO(l, event.getLatitude(), event.getLongitude()))
            .collect(Collectors.toList());
    }

    public List<CityGuideLocationDTO> getBusStops(Long eventId) {
        return getNearbyLocations(eventId, "BUS_STOP");
    }

    public List<CityGuideLocationDTO> getNearbyRestaurants(Long eventId) {
        return getNearbyLocations(eventId, "RESTAURANT");
    }

    public List<CityGuideLocationDTO> getParking(Long eventId) {
        return getNearbyLocations(eventId, "PARKING");
    }

    private CityGuideLocationDTO convertToDTO(CityGuideLocation location, Double eventLat, Double eventLon) {
        CityGuideLocationDTO dto = new CityGuideLocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setType(location.getType());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setAddress(location.getAddress());
        dto.setDescription(location.getDescription());
        dto.setImageUrl(location.getImageUrl());
        dto.setRating(location.getRating());
        dto.setPriceRange(location.getPriceRange());
        
        // Calculate distance
        double distance = calculateDistance(eventLat, eventLon, location.getLatitude(), location.getLongitude());
        dto.setDistance(distance);
        dto.setWalkingMinutes((int)(distance * 12)); // ~12 min per km
        
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
