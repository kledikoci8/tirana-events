package com.tirana.events.repository;

import com.tirana.events.model.FriendActivity;
import com.tirana.events.model.User;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FriendActivityRepository extends JpaRepository<FriendActivity, Long> {
    
    List<FriendActivity> findByUserOrderByTimestampDesc(User user);
    
    @Query("SELECT fa FROM FriendActivity fa " +
           "WHERE fa.user IN :friends " +
           "ORDER BY fa.timestamp DESC")
    List<FriendActivity> findFriendActivities(@Param("friends") List<User> friends);
    
    @Query("SELECT fa FROM FriendActivity fa " +
           "WHERE fa.event = :event " +
           "AND fa.user IN :friends " +
           "AND fa.activityType IN ('PURCHASED_TICKET', 'ATTENDING')")
    List<FriendActivity> findFriendsAttendingEvent(@Param("event") Event event, @Param("friends") List<User> friends);
    
    @Query("SELECT COUNT(fa) FROM FriendActivity fa " +
           "WHERE fa.event = :event " +
           "AND fa.user IN :friends " +
           "AND fa.activityType IN ('PURCHASED_TICKET', 'ATTENDING')")
    Long countFriendsAttendingEvent(@Param("event") Event event, @Param("friends") List<User> friends);
    
    List<FriendActivity> findByUserAndTimestampAfter(User user, LocalDateTime after);
}
