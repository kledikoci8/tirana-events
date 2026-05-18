package com.tirana.events.controller;

import com.tirana.events.dto.*;
import com.tirana.events.service.CommunityService;
import com.tirana.events.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CurrentUserService currentUserService;
    private final CommunityService communityService;

    @PostMapping("/posts")
    public ResponseEntity<CommunityPostDTO> createPost(
            @RequestBody CreateCommunityPostRequest request,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(communityService.createPost(userId, request));
    }

    @GetMapping("/boards/{boardType}")
    public ResponseEntity<List<CommunityPostDTO>> getPostsByBoard(@PathVariable String boardType) {
        return ResponseEntity.ok(communityService.getPostsByBoard(boardType));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<CommunityPostDTO>> getTrendingPosts() {
        return ResponseEntity.ok(communityService.getTrendingPosts());
    }

    @PostMapping("/posts/{postId}/upvote")
    public ResponseEntity<Void> upvotePost(@PathVariable Long postId) {
        communityService.upvotePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommunityCommentDTO> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        Long userId = currentUserService.requireUserId(auth);
        return ResponseEntity.ok(communityService.addComment(postId, userId, body.get("content")));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommunityCommentDTO>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPostComments(postId));
    }
}
