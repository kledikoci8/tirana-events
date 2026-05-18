package com.tirana.events.security;

import com.tirana.events.exception.UnauthorizedException;
import com.tirana.events.model.User;
import com.tirana.events.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Authentication required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    public Long requireUserId(Authentication authentication) {
        return requireUser(authentication).getId();
    }

    public User findUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    public Long findUserId(Authentication authentication) {
        User user = findUser(authentication);
        return user != null ? user.getId() : null;
    }
}
