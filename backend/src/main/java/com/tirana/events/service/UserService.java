package com.tirana.events.service;

import com.tirana.events.dto.UpdateUserRequest;
import com.tirana.events.dto.UserProfileDTO;
import com.tirana.events.model.User;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.TicketRepository;
import com.tirana.events.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public UserService(UserRepository userRepository,
                       TicketRepository ticketRepository,
                       EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public UserProfileDTO getProfile(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setProfileImage(user.getProfileImage());
        dto.setTicketsCount(ticketRepository.countByUserId(user.getId()));
        dto.setSavedCount(user.getSavedEvents() != null ? user.getSavedEvents().size() : 0);
        dto.setFollowingCount(user.getFollowing() != null ? user.getFollowing().size() : 0);
        dto.setEventsCount(eventRepository.countByOrganizerId(user.getId()));
        dto.setInterests(user.getInterests() != null
                ? user.getInterests().stream().map(c -> c.getName()).collect(Collectors.toList())
                : java.util.List.of());
        return dto;
    }

    @Transactional
    public UserProfileDTO updateProfile(User user, UpdateUserRequest request) {
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        return getProfile(userRepository.save(user));
    }
}
