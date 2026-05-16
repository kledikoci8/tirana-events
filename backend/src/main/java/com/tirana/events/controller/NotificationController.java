package com.tirana.events.controller;

import com.tirana.events.dto.NotificationHistoryDTO;
import com.tirana.events.dto.RegisterTokenRequest;
import com.tirana.events.dto.UpdateNotificationPreferencesRequest;
import com.tirana.events.model.DeviceToken;
import com.tirana.events.model.PushNotification;
import com.tirana.events.model.User;
import com.tirana.events.model.UserPreference;
import com.tirana.events.repository.UserPreferenceRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;
    
    @PostMapping("/register")
    public ResponseEntity<Void> registerToken(
            Authentication authentication,
            @RequestBody RegisterTokenRequest request) {
        
        User user = getCurrentUser(authentication);
        notificationService.registerToken(user, request.getToken(), request.getDeviceType());
        
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/preferences")
    public ResponseEntity<Void> updatePreferences(
            Authentication authentication,
            @RequestBody UpdateNotificationPreferencesRequest request) {
        
        User user = getCurrentUser(authentication);
        UserPreference pref = preferenceRepository.findByUser(user)
            .orElse(new UserPreference());
        
        pref.setUser(user);
        pref.setNotifyEventReminder(request.getNotifyEventReminder());
        pref.setNotifyPriceDrop(request.getNotifyPriceDrop());
        pref.setNotifyFriendActivity(request.getNotifyFriendActivity());
        pref.setNotifyNearbyEvents(request.getNotifyNearbyEvents());
        pref.setQuietHoursStart(request.getQuietHoursStart());
        pref.setQuietHoursEnd(request.getQuietHoursEnd());
        
        preferenceRepository.save(pref);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/preferences")
    public ResponseEntity<UpdateNotificationPreferencesRequest> getPreferences(Authentication authentication) {
        User user = getCurrentUser(authentication);
        UserPreference pref = preferenceRepository.findByUser(user)
            .orElse(new UserPreference());
        
        UpdateNotificationPreferencesRequest response = new UpdateNotificationPreferencesRequest();
        response.setNotifyEventReminder(pref.getNotifyEventReminder());
        response.setNotifyPriceDrop(pref.getNotifyPriceDrop());
        response.setNotifyFriendActivity(pref.getNotifyFriendActivity());
        response.setNotifyNearbyEvents(pref.getNotifyNearbyEvents());
        response.setQuietHoursStart(pref.getQuietHoursStart());
        response.setQuietHoursEnd(pref.getQuietHoursEnd());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<NotificationHistoryDTO>> getHistory(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<PushNotification> notifications = notificationService.getNotificationHistory(user);
        
        List<NotificationHistoryDTO> history = notifications.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(history);
    }
    
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private NotificationHistoryDTO convertToDTO(PushNotification notification) {
        NotificationHistoryDTO dto = new NotificationHistoryDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setStatus(notification.getStatus().name());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        if (notification.getEvent() != null) {
            dto.setEventId(notification.getEvent().getId());
            dto.setEventName(notification.getEvent().getName());
        }
        return dto;
    }
}
