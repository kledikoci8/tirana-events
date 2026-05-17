package com.tirana.events.service;

import com.tirana.events.dto.UserBadgeDTO;
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
public class BadgeService {
    private final UserBadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EventCheckInRepository checkInRepository;

    // Badge definitions
    private static final String CULTURE_VULTURE = "CULTURE_VULTURE"; // 5 cultural events
    private static final String NIGHT_OWL = "NIGHT_OWL"; // 10 events after 10pm
    private static final String TIRANA_ORIGINAL = "TIRANA_ORIGINAL"; // 20 events attended
    private static final String EARLY_BIRD = "EARLY_BIRD"; // 5 early bird tickets
    private static final String SOCIAL_BUTTERFLY = "SOCIAL_BUTTERFLY"; // 10 friends invited
    private static final String WEEKEND_WARRIOR = "WEEKEND_WARRIOR"; // 10 weekend events
    private static final String EXPLORER = "EXPLORER"; // 15 different venues

    @Transactional
    public void checkAndAwardBadges(Long userId) {
        checkCultureVulture(userId);
        checkNightOwl(userId);
        checkTiranaOriginal(userId);
        checkEarlyBird(userId);
        checkWeekendWarrior(userId);
    }

    private void checkCultureVulture(Long userId) {
        if (badgeRepository.existsByUserIdAndBadgeCode(userId, CULTURE_VULTURE)) return;
        
        long culturalEvents = ticketRepository.findByUserId(userId).stream()
            .filter(t -> t.getEvent().getCategory() != null && 
                        t.getEvent().getCategory().getName().toLowerCase().contains("culture"))
            .count();
        
        if (culturalEvents >= 5) {
            awardBadge(userId, CULTURE_VULTURE, "Culture Vulture", 
                "Attended 5 cultural events", "🎭");
        }
    }

    private void checkNightOwl(Long userId) {
        if (badgeRepository.existsByUserIdAndBadgeCode(userId, NIGHT_OWL)) return;
        
        long nightEvents = ticketRepository.findByUserId(userId).stream()
            .filter(t -> t.getEvent().getStartTime() != null && 
                        t.getEvent().getStartTime().getHour() >= 22)
            .count();
        
        if (nightEvents >= 10) {
            awardBadge(userId, NIGHT_OWL, "Night Owl", 
                "Attended 10 events after 10pm", "🦉");
        }
    }

    private void checkTiranaOriginal(Long userId) {
        if (badgeRepository.existsByUserIdAndBadgeCode(userId, TIRANA_ORIGINAL)) return;
        
        long totalEvents = checkInRepository.countByUserId(userId);
        
        if (totalEvents >= 20) {
            awardBadge(userId, TIRANA_ORIGINAL, "Tirana Original", 
                "Attended 20 events in Tirana", "⭐");
        }
    }

    private void checkEarlyBird(Long userId) {
        if (badgeRepository.existsByUserIdAndBadgeCode(userId, EARLY_BIRD)) return;
        
        // Check if user bought 5 early bird tickets (simplified)
        long earlyTickets = ticketRepository.findByUserId(userId).stream()
            .filter(t -> t.getPrice() != null && t.getPrice() < t.getEvent().getPrice())
            .count();
        
        if (earlyTickets >= 5) {
            awardBadge(userId, EARLY_BIRD, "Early Bird", 
                "Purchased 5 early bird tickets", "🐦");
        }
    }

    private void checkWeekendWarrior(Long userId) {
        if (badgeRepository.existsByUserIdAndBadgeCode(userId, WEEKEND_WARRIOR)) return;
        
        long weekendEvents = ticketRepository.findByUserId(userId).stream()
            .filter(t -> {
                if (t.getEvent().getStartTime() == null) return false;
                int dayOfWeek = t.getEvent().getStartTime().getDayOfWeek().getValue();
                return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
            })
            .count();
        
        if (weekendEvents >= 10) {
            awardBadge(userId, WEEKEND_WARRIOR, "Weekend Warrior", 
                "Attended 10 weekend events", "🎉");
        }
    }

    private void awardBadge(Long userId, String code, String name, String description, String icon) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        
        UserBadge badge = new UserBadge();
        badge.setUser(user);
        badge.setBadgeCode(code);
        badge.setBadgeName(name);
        badge.setDescription(description);
        badge.setIconUrl(icon);
        badge.setEarnedAt(LocalDateTime.now());
        badge.setIsDisplayed(true);
        
        badgeRepository.save(badge);
    }

    public List<UserBadgeDTO> getUserBadges(Long userId) {
        return badgeRepository.findByUserIdOrderByEarnedAtDesc(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private UserBadgeDTO convertToDTO(UserBadge badge) {
        UserBadgeDTO dto = new UserBadgeDTO();
        dto.setId(badge.getId());
        dto.setBadgeCode(badge.getBadgeCode());
        dto.setBadgeName(badge.getBadgeName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        dto.setEarnedAt(badge.getEarnedAt());
        dto.setIsDisplayed(badge.getIsDisplayed());
        
        // Check if earned in last 24h
        dto.setIsNew(badge.getEarnedAt().isAfter(LocalDateTime.now().minusDays(1)));
        
        return dto;
    }
}
