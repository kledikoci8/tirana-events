package com.tirana.events.repository;

import com.tirana.events.model.CuratedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuratedListRepository extends JpaRepository<CuratedList, Long> {
    List<CuratedList> findByCuratorIdAndIsPublishedTrueOrderByPublishedAtDesc(Long curatorId);
    
    @Query("SELECT cl FROM CuratedList cl WHERE cl.isPublished = true ORDER BY cl.publishedAt DESC")
    List<CuratedList> findAllPublishedOrderByPublishedAtDesc();
    
    @Query("SELECT cl FROM CuratedList cl WHERE cl.isPublished = true ORDER BY cl.viewsCount DESC")
    List<CuratedList> findTrendingLists();
}
