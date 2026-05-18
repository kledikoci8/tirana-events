package com.tirana.events.service;

import com.tirana.events.dto.EventMemoryDTO;
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
public class MemoryWallService {
    private final EventMemoryRepository memoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventCheckInRepository checkInRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public EventMemoryDTO uploadMemory(Long userId, Long eventId, String photoUrl, String caption) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Verify user has a ticket or check-in for this event
        boolean hasTicket = ticketRepository.existsByUserIdAndEventId(userId, eventId);
        boolean checkedIn = checkInRepository.findByTicketId(userId).isPresent();
        if (!hasTicket && !checkedIn) {
            throw new RuntimeException("You must attend the event to upload memories");
        }

        // Check if event ended within last 48 hours
        LocalDateTime eventEnd = event.getEndTime();
        LocalDateTime now = LocalDateTime.now();
        long hoursAfterEvent = java.time.Duration.between(eventEnd, now).toHours();
        
        if (hoursAfterEvent > 48) {
            throw new RuntimeException("Memory wall is only open for 48 hours after event");
        }

        EventMemory memory = new EventMemory();
        memory.setUser(user);
        memory.setEvent(event);
        memory.setPhotoUrl(photoUrl);
        memory.setCaption(caption);
        memory.setUploadedAt(LocalDateTime.now());
        memory.setIsApproved(false); // Requires moderation
        memory.setLikes(0);

        memory = memoryRepository.save(memory);

        return convertToDTO(memory, userId);
    }

    public List<EventMemoryDTO> getEventMemories(Long eventId, Long currentUserId) {
        return memoryRepository.findApprovedMemoriesByEventId(eventId)
                .stream()
                .map(m -> convertToDTO(m, currentUserId))
                .collect(Collectors.toList());
    }

    public List<EventMemoryDTO> getUserMemories(Long userId) {
        return memoryRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(m -> convertToDTO(m, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveMemory(Long memoryId) {
        EventMemory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new RuntimeException("Memory not found"));
        memory.setIsApproved(true);
        memoryRepository.save(memory);
    }

    @Transactional
    public void likeMemory(Long memoryId) {
        EventMemory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new RuntimeException("Memory not found"));
        memory.setLikes(memory.getLikes() + 1);
        memoryRepository.save(memory);
    }

    @Transactional
    public void reportMemory(Long memoryId) {
        EventMemory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new RuntimeException("Memory not found"));
        memory.setIsReported(true);
        memoryRepository.save(memory);
    }

    private EventMemoryDTO convertToDTO(EventMemory memory, Long currentUserId) {
        EventMemoryDTO dto = new EventMemoryDTO();
        dto.setId(memory.getId());
        dto.setUserId(memory.getUser().getId());
        dto.setUserName(memory.getUser().getFullName());
        dto.setUserAvatar(memory.getUser().getProfileImage());
        dto.setEventId(memory.getEvent().getId());
        dto.setPhotoUrl(memory.getPhotoUrl());
        dto.setCaption(memory.getCaption());
        dto.setUploadedAt(memory.getUploadedAt());
        dto.setLikes(memory.getLikes());
        dto.setIsLikedByCurrentUser(false); // TODO: Track individual likes
        return dto;
    }
}
