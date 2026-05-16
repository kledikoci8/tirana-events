package com.tirana.events.repository;

import com.tirana.events.model.PushNotification;
import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
    
    List<PushNotification> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT pn FROM PushNotification pn " +
           "WHERE pn.status = 'PENDING' " +
           "AND pn.scheduledFor <= :now")
    List<PushNotification> findPendingNotifications(@Param("now") LocalDateTime now);
    
    List<PushNotification> findByStatusAndScheduledForBefore(
        PushNotification.NotificationStatus status, 
        LocalDateTime before
    );
}
