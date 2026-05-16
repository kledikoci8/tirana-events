package com.tirana.events.scheduler;

import com.tirana.events.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotificationScheduler {
    
    private final NotificationService notificationService;
    
    /**
     * Process pending notifications every minute
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void processPendingNotifications() {
        notificationService.processPendingNotifications();
    }
}
