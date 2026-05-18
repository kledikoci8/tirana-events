package com.tirana.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FIX B3: Rate limiting configuration to prevent brute force and DoS attacks
 * 
 * This is a simple in-memory rate limiter. For production with multiple servers,
 * consider using Redis-based rate limiting (e.g., Bucket4j with Redis)
 */
@Configuration
public class RateLimitConfig {
    
    /**
     * Store for tracking request counts per IP address
     * Key: IP address
     * Value: RateLimitBucket containing count and reset time
     */
    @Bean
    public ConcurrentMap<String, RateLimitBucket> rateLimitStore() {
        return new ConcurrentHashMap<>();
    }
    
    /**
     * Inner class to track rate limit state for each IP
     */
    public static class RateLimitBucket {
        private int count;
        private long resetTime;
        
        public RateLimitBucket() {
            this.count = 0;
            this.resetTime = System.currentTimeMillis() + 60000; // 1 minute window
        }
        
        public synchronized boolean tryConsume(int maxRequests) {
            long now = System.currentTimeMillis();
            
            // Reset if window expired
            if (now > resetTime) {
                count = 0;
                resetTime = now + 60000;
            }
            
            // Check if under limit
            if (count < maxRequests) {
                count++;
                return true;
            }
            
            return false;
        }
        
        public int getCount() {
            return count;
        }
        
        public long getResetTime() {
            return resetTime;
        }
    }
}
