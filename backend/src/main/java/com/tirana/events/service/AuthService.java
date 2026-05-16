package com.tirana.events.service;

import com.tirana.events.dto.AuthRequest;
import com.tirana.events.dto.AuthResponse;
import com.tirana.events.dto.RegisterRequest;
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
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName());
    }
}
