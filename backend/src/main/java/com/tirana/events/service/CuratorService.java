package com.tirana.events.service;

import com.tirana.events.dto.*;
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
public class CuratorService {
    private final CuratorRepository curatorRepository;
    private final CuratedListRepository listRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CuratorDTO createCurator(Long userId, String displayName, String bio) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Curator curator = new Curator();
        curator.setUser(user);
        curator.setDisplayName(displayName);
        curator.setBio(bio);
        curator.setIsVerified(false);
        curator.setCreatedAt(LocalDateTime.now());
        
        curator = curatorRepository.save(curator);
        
        return convertCuratorToDTO(curator);
    }

    @Transactional
    public CuratedListDTO createCuratedList(Long curatorId, CreateCuratedListRequest request) {
        Curator curator = curatorRepository.findById(curatorId)
            .orElseThrow(() -> new RuntimeException("Curator not found"));
        
        CuratedList list = new CuratedList();
        list.setCurator(curator);
        list.setTitle(request.getTitle());
        list.setDescription(request.getDescription());
        list.setCoverImageUrl(request.getCoverImageUrl());
        list.setIsPublished(false);
        list.setCreatedAt(LocalDateTime.now());
        
        // Add events
        List<Event> events = eventRepository.findAllById(request.getEventIds());
        list.setEvents(events);
        
        list = listRepository.save(list);
        
        return convertListToDTO(list);
    }

    @Transactional
    public void publishList(Long listId) {
        CuratedList list = listRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("List not found"));
        
        list.setIsPublished(true);
        list.setPublishedAt(LocalDateTime.now());
        listRepository.save(list);
        
        // Update curator stats
        Curator curator = list.getCurator();
        curator.setCuratedListsCount(curator.getCuratedListsCount() + 1);
        curatorRepository.save(curator);
    }

    public List<CuratorDTO> getVerifiedCurators() {
        return curatorRepository.findVerifiedCuratorsOrderByFollowers()
            .stream()
            .map(this::convertCuratorToDTO)
            .collect(Collectors.toList());
    }

    public List<CuratedListDTO> getCuratorLists(Long curatorId) {
        return listRepository.findByCuratorIdAndIsPublishedTrueOrderByPublishedAtDesc(curatorId)
            .stream()
            .map(this::convertListToDTO)
            .collect(Collectors.toList());
    }

    public List<CuratedListDTO> getTrendingLists() {
        return listRepository.findTrendingLists()
            .stream()
            .limit(10)
            .map(this::convertListToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void trackListView(Long listId) {
        CuratedList list = listRepository.findById(listId).orElse(null);
        if (list != null) {
            list.setViewsCount(list.getViewsCount() + 1);
            listRepository.save(list);
        }
    }

    private CuratorDTO convertCuratorToDTO(Curator curator) {
        CuratorDTO dto = new CuratorDTO();
        dto.setId(curator.getId());
        dto.setUserId(curator.getUser().getId());
        dto.setDisplayName(curator.getDisplayName());
        dto.setBio(curator.getBio());
        dto.setAvatarUrl(curator.getAvatarUrl());
        dto.setCoverImageUrl(curator.getCoverImageUrl());
        dto.setIsVerified(curator.getIsVerified());
        dto.setFollowersCount(curator.getFollowersCount());
        dto.setCuratedListsCount(curator.getCuratedListsCount());
        dto.setTotalTicketsSold(curator.getTotalTicketsSold());
        return dto;
    }

    private CuratedListDTO convertListToDTO(CuratedList list) {
        CuratedListDTO dto = new CuratedListDTO();
        dto.setId(list.getId());
        dto.setCurator(convertCuratorToDTO(list.getCurator()));
        dto.setTitle(list.getTitle());
        dto.setDescription(list.getDescription());
        dto.setCoverImageUrl(list.getCoverImageUrl());
        dto.setViewsCount(list.getViewsCount());
        dto.setSavesCount(list.getSavesCount());
        dto.setTicketsSold(list.getTicketsSold());
        dto.setPublishedAt(list.getPublishedAt());
        
        // Convert events (simplified)
        if (list.getEvents() != null) {
            dto.setEvents(list.getEvents().stream()
                .map(this::convertEventToDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private EventDTO convertEventToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setImageUrl(event.getImageUrl());
        dto.setStartTime(event.getStartTime());
        dto.setVenue(event.getVenue());
        dto.setPrice(event.getPrice());
        return dto;
    }
}
