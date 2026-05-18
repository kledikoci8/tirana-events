package com.tirana.events.service;

import com.tirana.events.dto.AuthRequest;
import com.tirana.events.dto.AuthResponse;
import com.tirana.events.dto.RegisterRequest;
import com.tirana.events.exception.ResourceNotFoundException;
import com.tirana.events.exception.ValidationException;
import com.tirana.events.model.User;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        // FIX B5: Enforce password strength requirements
        validatePasswordStrength(request.getPassword());
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return new AuthResponse(token, refreshToken, user.getId(), user.getEmail(), user.getFullName());
    }
    
    /**
     * FIX B5: Validate password strength
     * Requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain at least one number");
        }
        
        // Optional: Check for special characters
        // if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
        //     throw new ValidationException("Password must contain at least one special character");
        // }
    }
    
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return new AuthResponse(token, refreshToken, user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        String email = jwtUtil.extractUsername(refreshToken);
        
        if (!jwtUtil.validateToken(refreshToken, email)) {
            throw new ValidationException("Invalid refresh token");
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String newToken = jwtUtil.generateToken(user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        return new AuthResponse(newToken, newRefreshToken, user.getId(), user.getEmail(), user.getFullName());
    }
}
