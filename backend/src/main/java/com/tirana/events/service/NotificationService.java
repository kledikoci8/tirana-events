package com.tirana.events.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final DeviceTokenRepository tokenRepository;
    private final PushNotificationRepository notificationRepository;
    private final UserPreferenceRepository preferenceRepository;
    
    /**
     * Register device token for push notifications
     */
    @Transactional
    public DeviceToken registerToken(User user, String token, DeviceToken.DeviceType deviceType) {
        // Check if token already exists
        DeviceToken existingToken = tokenRepository.findByToken(token).orElse(null);
        
        if (existingToken != null) {
            existingToken.setUser(user);
            existingToken.setIsActive(true);
            existingToken.setLastUsedAt(LocalDateTime.now());
            return tokenRepository.save(existingToken);
        }
        
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setUser(user);
        deviceToken.setToken(token);
        deviceToken.setDeviceType(deviceType);
        deviceToken.setIsActive(true);
        
        return tokenRepository.save(deviceToken);
    }
    
    /**
     * Schedule notification
     */
    @Transactional
    public PushNotification scheduleNotification(
            User user,
            Event event,
            PushNotification.NotificationType type,
            String title,
            String body,
            LocalDateTime scheduledFor) {
        
        // Check user preferences
        if (!shouldSendNotification(user, type, scheduledFor)) {
            return null;
        }
        
        PushNotification notification = new PushNotification();
        notification.setUser(user);
        notification.setEvent(event);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setScheduledFor(scheduledFor);
        notification.setStatus(PushNotification.NotificationStatus.PENDING);
        
        if (event != null) {
            notification.setDeepLink("tiranaevents://event/" + event.getId());
        }
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send notification immediately
     */
    @Transactional
    public void sendNotification(PushNotification notification) {
        if (FirebaseApp.getApps().isEmpty()) {
            // In-app delivery: notification is stored in MySQL for the client to fetch
            notification.setStatus(PushNotification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);
            notificationRepository.save(notification);
            return;
        }
        
        List<DeviceToken> tokens = tokenRepository.findByUserAndIsActiveTrue(notification.getUser());
        
        if (tokens.isEmpty()) {
            notification.setStatus(PushNotification.NotificationStatus.FAILED);
            notification.setErrorMessage("No active device tokens");
            notificationRepository.save(notification);
            return;
        }
        
        for (DeviceToken deviceToken : tokens) {
            try {
                Message message = buildMessage(notification, deviceToken.getToken());
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                // Log error but continue with other tokens
                System.err.println("Failed to send notification to token: " + deviceToken.getToken());
            }
        }
        
        notification.setStatus(PushNotification.NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
    
    /**
     * Process pending notifications
     */
    @Transactional
    public void processPendingNotifications() {
        List<PushNotification> pending = notificationRepository.findPendingNotifications(LocalDateTime.now());
        
        for (PushNotification notification : pending) {
            sendNotification(notification);
        }
    }
    
    /**
     * Schedule event reminders
     */
    @Transactional
    public void scheduleEventReminders(Event event, List<User> attendees) {
        LocalDateTime eventTime = event.getStartDate();
        LocalDateTime reminder24h = eventTime.minusHours(24);
        LocalDateTime reminder2h = eventTime.minusHours(2);
        
        for (User user : attendees) {
            // 24-hour reminder
            scheduleNotification(
                user,
                event,
                PushNotification.NotificationType.EVENT_REMINDER_24H,
                "Event Tomorrow!",
                event.getName() + " starts tomorrow at " + eventTime.toLocalTime(),
                reminder24h
            );
            
            // 2-hour reminder
            scheduleNotification(
                user,
                event,
                PushNotification.NotificationType.EVENT_REMINDER_2H,
                "Event Starting Soon!",
                event.getName() + " starts in 2 hours",
                reminder2h
            );
        }
    }
    
    /**
     * Send price drop notification
     */
    @Transactional
    public void notifyPriceDrop(Event event, List<User> interestedUsers, Double oldPrice, Double newPrice) {
        for (User user : interestedUsers) {
            scheduleNotification(
                user,
                event,
                PushNotification.NotificationType.PRICE_DROP,
                "Price Drop Alert!",
                event.getName() + " price reduced from " + oldPrice + " to " + newPrice + " ALL",
                LocalDateTime.now()
            );
        }
    }
    
    /**
     * Send friend activity notification
     */
    @Transactional
    public void notifyFriendActivity(User user, User friend, Event event) {
        scheduleNotification(
            user,
            event,
            PushNotification.NotificationType.FRIEND_ACTIVITY,
            "Friend Activity",
            friend.getFullName() + " just bought a ticket to " + event.getName(),
            LocalDateTime.now()
        );
    }
    
    /**
     * Send nearby event notification
     */
    @Transactional
    public void notifyNearbyEvent(User user, Event event, double distance) {
        scheduleNotification(
            user,
            event,
            PushNotification.NotificationType.NEARBY_EVENT,
            "Event Happening Near You!",
            event.getName() + " is happening " + String.format("%.1f", distance) + "km from you right now",
            LocalDateTime.now()
        );
    }
    
    /**
     * Check if notification should be sent based on user preferences
     */
    private boolean shouldSendNotification(User user, PushNotification.NotificationType type, LocalDateTime scheduledFor) {
        UserPreference pref = preferenceRepository.findByUser(user).orElse(null);
        
        if (pref == null) {
            return true; // Default to sending if no preferences set
        }
        
        // Check notification type preferences
        switch (type) {
            case EVENT_REMINDER_24H:
            case EVENT_REMINDER_2H:
                if (!pref.getNotifyEventReminder()) return false;
                break;
            case PRICE_DROP:
                if (!pref.getNotifyPriceDrop()) return false;
                break;
            case FRIEND_ACTIVITY:
                if (!pref.getNotifyFriendActivity()) return false;
                break;
            case NEARBY_EVENT:
                if (!pref.getNotifyNearbyEvents()) return false;
                break;
        }
        
        // Check quiet hours
        if (scheduledFor != null) {
            int hour = scheduledFor.getHour();
            int quietStart = pref.getQuietHoursStart();
            int quietEnd = pref.getQuietHoursEnd();
            
            if (quietStart > quietEnd) {
                // Overnight quiet hours (e.g., 23:00 to 08:00)
                if (hour >= quietStart || hour < quietEnd) {
                    return false;
                }
            } else {
                // Normal quiet hours (e.g., 13:00 to 15:00)
                if (hour >= quietStart && hour < quietEnd) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Build FCM message
     */
    private Message buildMessage(PushNotification notification, String token) {
        Map<String, String> data = new HashMap<>();
        data.put("type", notification.getType().name());
        if (notification.getDeepLink() != null) {
            data.put("deepLink", notification.getDeepLink());
        }
        if (notification.getEvent() != null) {
            data.put("eventId", notification.getEvent().getId().toString());
        }
        
        return Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getBody())
                .build())
            .putAllData(data)
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .setSound("default")
                    .build())
                .build())
            .setAndroidConfig(AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                    .setSound("default")
                    .setColor("#7F77DD")
                    .build())
                .build())
            .build();
    }
    
    /**
     * Get notification history for user
     */
    public List<PushNotification> getNotificationHistory(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
