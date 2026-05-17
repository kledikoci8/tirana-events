package com.tirana.events.repository;

import com.tirana.events.model.LiveStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveStreamRepository extends JpaRepository<LiveStream, Long> {
    Optional<LiveStream> findByEventId(Long eventId);
    
    @Query("SELECT ls FROM LiveStream ls WHERE ls.isLive = true ORDER BY ls.viewersCount DESC")
    List<LiveStream> findActiveLiveStreams();
}
