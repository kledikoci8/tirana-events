package com.tirana.events.security;

import com.tirana.events.config.RateLimitConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentMap;

/**
 * FIX B3: Rate limiting interceptor to prevent brute force attacks
 * 
 * Limits:
 * - Auth endpoints (login/register): 5 requests per minute per IP
 * - Ticket purchase: 10 requests per minute per IP
 * - Other endpoints: 100 requests per minute per IP
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Autowired
    private ConcurrentMap<String, RateLimitConfig.RateLimitBucket> rateLimitStore;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);
        String requestUri = request.getRequestURI();
        
        // Determine rate limit based on endpoint
        int maxRequests = getMaxRequests(requestUri);
        
        // Get or create bucket for this IP
        RateLimitConfig.RateLimitBucket bucket = rateLimitStore.computeIfAbsent(
            clientIp + ":" + getCategoryKey(requestUri),
            k -> new RateLimitConfig.RateLimitBucket()
        );
        
        // Try to consume from bucket
        if (!bucket.tryConsume(maxRequests)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Try again in %d seconds.\",\"retryAfter\":%d}",
                (bucket.getResetTime() - System.currentTimeMillis()) / 1000,
                (bucket.getResetTime() - System.currentTimeMillis()) / 1000
            ));
            return false;
        }
        
        return true;
    }
    
    private int getMaxRequests(String uri) {
        if (uri.contains("/auth/login") || uri.contains("/auth/register")) {
            return 5; // Strict limit for auth endpoints
        } else if (uri.contains("/tickets/purchase")) {
            return 10; // Moderate limit for ticket purchase
        } else {
            return 100; // Generous limit for other endpoints
        }
    }
    
    private String getCategoryKey(String uri) {
        if (uri.contains("/auth/")) {
            return "auth";
        } else if (uri.contains("/tickets/purchase")) {
            return "purchase";
        } else {
            return "general";
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
