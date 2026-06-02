package com.tirana.events.service;

import com.tirana.events.dto.AuthRequest;
import com.tirana.events.dto.AuthResponse;
import com.tirana.events.dto.RegisterRequest;
import com.tirana.events.exception.ResourceNotFoundException;
import com.tirana.events.exception.ValidationException;
import com.tirana.events.model.RefreshToken;
import com.tirana.events.model.User;
import com.tirana.events.repository.RefreshTokenRepository;
import com.tirana.events.repository.UserRepository;
import com.tirana.events.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
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
    
    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // FIX B7: Revoke all existing refresh tokens for this user
        refreshTokenRepository.revokeAllByUserEmail(user.getEmail(), LocalDateTime.now());
        
        String token = jwtUtil.generateToken(user.getEmail());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());
        
        // FIX B7: Store refresh token in database
        RefreshToken refreshToken = new RefreshToken(
            refreshTokenValue,
            user.getEmail(),
            LocalDateTime.now().plusDays(30) // 30 days expiry
        );
        refreshTokenRepository.save(refreshToken);
        
        return new AuthResponse(token, refreshTokenValue, user.getId(), user.getEmail(), user.getFullName());
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        // FIX B7: Validate refresh token from database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ValidationException("Invalid refresh token"));
        
        if (!refreshToken.isValid()) {
            throw new ValidationException("Refresh token is expired or revoked");
        }
        
        User user = userRepository.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Generate new tokens
        String newToken = jwtUtil.generateToken(user.getEmail());
        String newRefreshTokenValue = jwtUtil.generateRefreshToken(user.getEmail());
        
        // FIX B7: Revoke old refresh token and create new one
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        
        RefreshToken newRefreshToken = new RefreshToken(
            newRefreshTokenValue,
            user.getEmail(),
            LocalDateTime.now().plusDays(30)
        );
        refreshTokenRepository.save(newRefreshToken);
        
        return new AuthResponse(newToken, newRefreshTokenValue, user.getId(), user.getEmail(), user.getFullName());
    }
    
    /**
     * FIX B7: Logout method to revoke refresh tokens
     */
    @Transactional
    public void logout(String userEmail) {
        refreshTokenRepository.revokeAllByUserEmail(userEmail, LocalDateTime.now());
    }
}
