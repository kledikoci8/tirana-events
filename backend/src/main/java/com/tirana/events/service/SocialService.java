package com.tirana.events.service;

import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialService {
    
    private final FriendActivityRepository activityRepository;
    private final EventChatRepository chatRepository;
    private final EventInviteRepository inviteRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    /**
     * Record friend activity
     */
    @Transactional
    public void recordActivity(User user, Event event, FriendActivity.ActivityType activityType) {
        FriendActivity activity = new FriendActivity();
        activity.setUser(user);
        activity.setEvent(event);
        activity.setActivityType(activityType);
        activity.setTimestamp(LocalDateTime.now());
        activityRepository.save(activity);
    }
    
    /**
     * Get friend activity feed for a user
     */
    public List<FriendActivity> getFriendActivityFeed(User user, int limit) {
        // Get user's friends (people they follow)
        List<User> friends = user.getFollowing().stream().collect(Collectors.toList());
        
        if (friends.isEmpty()) {
            return List.of();
        }
        
        return activityRepository.findFriendActivities(friends).stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Get friends attending a specific event
     */
    public List<User> getFriendsAttendingEvent(User user, Event event) {
        List<User> friends = user.getFollowing().stream().collect(Collectors.toList());
        
        if (friends.isEmpty()) {
            return List.of();
        }
        
        return activityRepository.findFriendsAttendingEvent(event, friends).stream()
            .map(FriendActivity::getUser)
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Count friends attending an event
     */
    public Long countFriendsAttendingEvent(User user, Event event) {
        List<User> friends = user.getFollowing().stream().collect(Collectors.toList());
        
        if (friends.isEmpty()) {
            return 0L;
        }
        
        return activityRepository.countFriendsAttendingEvent(event, friends);
    }
    
    /**
     * Send chat message to event group
     */
    @Transactional
    public EventChat sendChatMessage(User user, Event event, String message, Long replyToId) {
        // Verify user has ticket to this event
        boolean hasTicket = ticketRepository.existsByUserAndEvent(user, event);
        if (!hasTicket) {
            throw new RuntimeException("User must have a ticket to chat in this event");
        }
        
        EventChat chat = new EventChat();
        chat.setUser(user);
        chat.setEvent(event);
        chat.setMessage(message);
        chat.setTimestamp(LocalDateTime.now());
        
        if (replyToId != null) {
            chatRepository.findById(replyToId).ifPresent(chat::setReplyTo);
        }
        
        return chatRepository.save(chat);
    }
    
    /**
     * Get chat messages for an event
     */
    public List<EventChat> getEventChatMessages(Event event, LocalDateTime since) {
        if (since != null) {
            return chatRepository.findByEventAndTimestampAfterOrderByTimestampAsc(event, since);
        }
        return chatRepository.findByEventOrderByTimestampAsc(event);
    }
    
    /**
     * Create event invite with deep link
     */
    @Transactional
    public EventInvite createInvite(User inviter, Event event, User invitee, String email, String phone) {
        EventInvite invite = new EventInvite();
        invite.setInviter(inviter);
        invite.setEvent(event);
        invite.setInvitee(invitee);
        invite.setInviteeEmail(email);
        invite.setInviteePhone(phone);
        invite.setInviteToken(UUID.randomUUID().toString());
        invite.setStatus(EventInvite.InviteStatus.PENDING);
        invite.setCreatedAt(LocalDateTime.now());
        
        return inviteRepository.save(invite);
    }
    
    /**
     * Accept invite via token
     */
    @Transactional
    public EventInvite acceptInvite(String token, User acceptingUser) {
        EventInvite invite = inviteRepository.findByInviteToken(token)
            .orElseThrow(() -> new RuntimeException("Invite not found"));
        
        if (invite.getStatus() != EventInvite.InviteStatus.PENDING) {
            throw new RuntimeException("Invite already " + invite.getStatus());
        }
        
        invite.setStatus(EventInvite.InviteStatus.ACCEPTED);
        invite.setAcceptedAt(LocalDateTime.now());
        
        if (invite.getInvitee() == null) {
            invite.setInvitee(acceptingUser);
        }
        
        // Record activity
        recordActivity(acceptingUser, invite.getEvent(), FriendActivity.ActivityType.INTERESTED);
        
        return inviteRepository.save(invite);
    }
    
    /**
     * Get deep link URL for event invite
     */
    public String getInviteDeepLink(EventInvite invite) {
        return String.format("tiranaevents://event/%d?invite=%s", 
            invite.getEvent().getId(), 
            invite.getInviteToken());
    }
    
    /**
     * Follow/unfollow a user
     */
    @Transactional
    public void followUser(User follower, User userToFollow) {
        follower.getFollowing().add(userToFollow);
        userRepository.save(follower);
    }
    
    @Transactional
    public void unfollowUser(User follower, User userToUnfollow) {
        follower.getFollowing().remove(userToUnfollow);
        userRepository.save(follower);
    }
    
    /**
     * Get user's friends list
     */
    public List<User> getFriends(User user) {
        return user.getFollowing().stream().collect(Collectors.toList());
    }
}
