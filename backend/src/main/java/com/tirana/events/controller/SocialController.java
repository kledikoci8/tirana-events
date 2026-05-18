package com.tirana.events.controller;

import com.tirana.events.dto.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import com.tirana.events.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {
    
    private final SocialService socialService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    
    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getFriends(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<User> friends = socialService.getFriends(user);
        
        List<UserDTO> friendDTOs = friends.stream()
            .map(this::convertUserToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(friendDTOs);
    }
    
    @PostMapping("/friends/{userId}/follow")
    public ResponseEntity<Void> followUser(
            Authentication authentication,
            @PathVariable Long userId) {
        
        User user = getCurrentUser(authentication);
        User userToFollow = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        socialService.followUser(user, userToFollow);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/friends/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(
            Authentication authentication,
            @PathVariable Long userId) {
        
        User user = getCurrentUser(authentication);
        User userToUnfollow = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        socialService.unfollowUser(user, userToUnfollow);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/activity/feed")
    public ResponseEntity<List<FriendActivityDTO>> getActivityFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "50") int limit) {
        
        User user = getCurrentUser(authentication);
        List<FriendActivity> activities = socialService.getFriendActivityFeed(user, limit);
        
        List<FriendActivityDTO> activityDTOs = activities.stream()
            .map(this::convertActivityToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(activityDTOs);
    }
    
    @GetMapping("/events/{eventId}/attendees")
    public ResponseEntity<FriendsAttendingDTO> getFriendsAttending(
            Authentication authentication,
            @PathVariable Long eventId) {
        
        User user = getCurrentUser(authentication);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        List<User> friendsAttending = socialService.getFriendsAttendingEvent(user, event);
        Long count = socialService.countFriendsAttendingEvent(user, event);
        
        FriendsAttendingDTO dto = new FriendsAttendingDTO();
        dto.setCount(count);
        dto.setFriends(friendsAttending.stream()
            .map(this::convertUserToDTO)
            .collect(Collectors.toList()));
        
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/events/{eventId}/chat")
    public ResponseEntity<EventChatDTO> sendChatMessage(
            Authentication authentication,
            @PathVariable Long eventId,
            @RequestBody SendChatRequest request) {
        
        User user = getCurrentUser(authentication);
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        EventChat chat = socialService.sendChatMessage(
            user, event, request.getMessage(), request.getReplyToId()
        );
        
        return ResponseEntity.ok(convertChatToDTO(chat));
    }
    
    @GetMapping("/events/{eventId}/chat")
    public ResponseEntity<List<EventChatDTO>> getChatMessages(
            Authentication authentication,
            @PathVariable Long eventId,
            @RequestParam(required = false) String since) {
        
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        LocalDateTime sinceTime = since != null ? LocalDateTime.parse(since) : null;
        List<EventChat> messages = socialService.getEventChatMessages(event, sinceTime);
        
        List<EventChatDTO> messageDTOs = messages.stream()
            .map(this::convertChatToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(messageDTOs);
    }
    
    @PostMapping("/invites/create")
    public ResponseEntity<EventInviteDTO> createInvite(
            Authentication authentication,
            @RequestBody CreateInviteRequest request) {
        
        User inviter = getCurrentUser(authentication);
        Event event = eventRepository.findById(request.getEventId())
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User invitee = null;
        if (request.getInviteeId() != null) {
            invitee = userRepository.findById(request.getInviteeId())
                .orElse(null);
        }
        
        EventInvite invite = socialService.createInvite(
            inviter, event, invitee, request.getEmail(), request.getPhone()
        );
        
        EventInviteDTO dto = convertInviteToDTO(invite);
        dto.setDeepLink(socialService.getInviteDeepLink(invite));
        
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<EventInviteDTO> acceptInvite(
            Authentication authentication,
            @PathVariable String token) {
        
        User user = getCurrentUser(authentication);
        EventInvite invite = socialService.acceptInvite(token, user);
        
        return ResponseEntity.ok(convertInviteToDTO(invite));
    }
    
    @GetMapping("/invites/{token}")
    public ResponseEntity<EventInviteDTO> getInviteDetails(@PathVariable String token) {
        EventInvite invite = socialService.getInviteByToken(token);
        EventInviteDTO dto = convertInviteToDTO(invite);
        dto.setDeepLink(socialService.getInviteDeepLink(invite));
        return ResponseEntity.ok(dto);
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private UserDTO convertUserToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setProfileImage(user.getProfileImage());
        return dto;
    }
    
    private FriendActivityDTO convertActivityToDTO(FriendActivity activity) {
        FriendActivityDTO dto = new FriendActivityDTO();
        dto.setId(activity.getId());
        dto.setUser(convertUserToDTO(activity.getUser()));
        dto.setEventId(activity.getEvent().getId());
        dto.setEventName(activity.getEvent().getName());
        dto.setEventImageUrl(activity.getEvent().getImageUrl());
        dto.setActivityType(activity.getActivityType().name());
        dto.setTimestamp(activity.getTimestamp());
        return dto;
    }
    
    private EventChatDTO convertChatToDTO(EventChat chat) {
        EventChatDTO dto = new EventChatDTO();
        dto.setId(chat.getId());
        dto.setUser(convertUserToDTO(chat.getUser()));
        dto.setMessage(chat.getMessage());
        dto.setTimestamp(chat.getTimestamp());
        if (chat.getReplyTo() != null) {
            dto.setReplyToId(chat.getReplyTo().getId());
        }
        return dto;
    }
    
    private EventInviteDTO convertInviteToDTO(EventInvite invite) {
        EventInviteDTO dto = new EventInviteDTO();
        dto.setId(invite.getId());
        dto.setEventId(invite.getEvent().getId());
        dto.setEventName(invite.getEvent().getName());
        dto.setInviter(convertUserToDTO(invite.getInviter()));
        if (invite.getInvitee() != null) {
            dto.setInvitee(convertUserToDTO(invite.getInvitee()));
        }
        dto.setStatus(invite.getStatus().name());
        dto.setCreatedAt(invite.getCreatedAt());
        return dto;
    }
}
