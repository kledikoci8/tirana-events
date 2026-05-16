package com.tirana.events.repository;

import com.tirana.events.model.UserInteraction;
import com.tirana.events.model.User;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    
    List<UserInteraction> findByUserOrderByTimestampDesc(User user);
    
    List<UserInteraction> findByUserAndTimestampAfter(User user, LocalDateTime after);
    
    @Query("SELECT ui.event.category.id, SUM(ui.weight) as totalWeight " +
           "FROM UserInteraction ui " +
           "WHERE ui.user = :user " +
           "GROUP BY ui.event.category.id " +
           "ORDER BY totalWeight DESC")
    List<Object[]> findTopCategoriesByUser(@Param("user") User user);
    
    @Query("SELECT ui.event FROM UserInteraction ui " +
           "WHERE ui.user = :user " +
           "AND ui.type IN ('PURCHASE', 'SAVE') " +
           "ORDER BY ui.timestamp DESC")
    List<Event> findUserEngagedEvents(@Param("user") User user);
    
    @Query("SELECT DISTINCT ui2.event FROM UserInteraction ui1 " +
           "JOIN UserInteraction ui2 ON ui1.event.category = ui2.event.category " +
           "WHERE ui1.user = :user " +
           "AND ui2.user != :user " +
           "AND ui1.type IN ('PURCHASE', 'SAVE') " +
           "AND ui2.type IN ('PURCHASE', 'SAVE') " +
           "AND ui2.event.startDate > :now " +
           "ORDER BY ui2.timestamp DESC")
    List<Event> findCollaborativeFilteredEvents(@Param("user") User user, @Param("now") LocalDateTime now);
}
