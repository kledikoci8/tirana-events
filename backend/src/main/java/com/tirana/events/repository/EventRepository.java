package com.tirana.events.repository;

import com.tirana.events.model.Event;
import com.tirana.events.model.Category;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(Category category);
    List<Event> findByStartDateAfter(LocalDateTime date);
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT e FROM Event e WHERE e.startDate >= :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);
    
    // FIX E1: Add pagination at database level using Spring Data Pageable
    @Query("SELECT e FROM Event e WHERE e.startDate >= :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingEventsWithPagination(@Param("now") LocalDateTime now, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Event> searchEvents(@Param("query") String query);
    
    // FIX E1: Add pagination to search using Spring Data Pageable
    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Event> searchEventsWithPagination(@Param("query") String query, Pageable pageable);
    
    // For personalization
    List<Event> findByCategoryInAndStartDateAfter(List<Category> categories, LocalDateTime after);
    
    List<Event> findByCategoryIdInAndStartDateAfter(List<Long> categoryIds, LocalDateTime after);
    
    List<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    long countByOrganizerId(Long organizerId);

    List<Event> findByOrganizerIdOrderByStartDateDesc(Long organizerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
