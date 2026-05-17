package com.tirana.events.repository;

import com.tirana.events.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByBoardTypeOrderByCreatedAtDesc(String boardType);
    
    @Query("SELECT p FROM CommunityPost p WHERE p.isModerated = true ORDER BY p.isPinned DESC, p.upvotes DESC, p.createdAt DESC")
    List<CommunityPost> findTrendingPosts();
    
    List<CommunityPost> findByEventIdOrderByCreatedAtDesc(Long eventId);
    
    List<CommunityPost> findByUserIdOrderByCreatedAtDesc(Long userId);
}
