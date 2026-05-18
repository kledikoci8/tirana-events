package com.tirana.events.controller;

import com.tirana.events.dto.UpdateUserRequest;
import com.tirana.events.dto.UserDTO;
import com.tirana.events.dto.UserProfileDTO;
import com.tirana.events.model.User;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.security.CurrentUserService;
import com.tirana.events.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// FIX B1: Removed @CrossOrigin - CORS is handled globally in SecurityConfig
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CurrentUserService currentUserService;
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(CurrentUserService currentUserService, UserService userService,
                            UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String q) {
        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }
        String query = q.trim();
        List<UserDTO> results = userRepository
                .findTop20ByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setFullName(u.getFullName());
                    dto.setEmail(u.getEmail());
                    dto.setProfileImage(u.getProfileImage());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        User user = currentUserService.requireUser(authentication);
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDTO> updateCurrentUser(
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        User user = currentUserService.requireUser(authentication);
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }
}
