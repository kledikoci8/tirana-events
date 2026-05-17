package com.tirana.events.service;

import com.tirana.events.dto.*;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommunityPostDTO createPost(Long userId, CreateCommunityPostRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setBoardType(request.getBoardType());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        
        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId()).orElse(null);
            post.setEvent(event);
        }
        
        post.setCreatedAt(LocalDateTime.now());
        post.setIsModerated(true);
        post.setIsPinned(false);
        
        post = postRepository.save(post);
        
        return convertPostToDTO(post);
    }

    public List<CommunityPostDTO> getPostsByBoard(String boardType) {
        return postRepository.findByBoardTypeOrderByCreatedAtDesc(boardType)
            .stream()
            .map(this::convertPostToDTO)
            .collect(Collectors.toList());
    }

    public List<CommunityPostDTO> getTrendingPosts() {
        return postRepository.findTrendingPosts()
            .stream()
            .limit(20)
            .map(this::convertPostToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void upvotePost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setUpvotes(post.getUpvotes() + 1);
        postRepository.save(post);
    }

    @Transactional
    public CommunityCommentDTO addComment(Long postId, Long userId, String content) {
        CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        
        comment = commentRepository.save(comment);
        
        // Update post comment count
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        return convertCommentToDTO(comment);
    }

    public List<CommunityCommentDTO> getPostComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
            .stream()
            .map(this::convertCommentToDTO)
            .collect(Collectors.toList());
    }

    private CommunityPostDTO convertPostToDTO(CommunityPost post) {
        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setUserName(post.getUser().getName());
        dto.setUserAvatar(post.getUser().getProfilePicture());
        
        if (post.getEvent() != null) {
            dto.setEventId(post.getEvent().getId());
            dto.setEventName(post.getEvent().getName());
        }
        
        dto.setBoardType(post.getBoardType());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setUpvotes(post.getUpvotes());
        dto.setCommentsCount(post.getCommentsCount());
        dto.setIsPinned(post.getIsPinned());
        dto.setCreatedAt(post.getCreatedAt());
        
        return dto;
    }

    private CommunityCommentDTO convertCommentToDTO(CommunityComment comment) {
        CommunityCommentDTO dto = new CommunityCommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setUserAvatar(comment.getUser().getProfilePicture());
        dto.setContent(comment.getContent());
        dto.setUpvotes(comment.getUpvotes());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
