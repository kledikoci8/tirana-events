package com.tirana.events.service;

import com.tirana.events.dto.EventDTO;
import com.tirana.events.dto.MoodSearchRequest;
import com.tirana.events.dto.MoodSearchResultDTO;
import com.tirana.events.model.*;
import com.tirana.events.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodSearchService {
    private final MoodSearchRepository moodSearchRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // Mood keywords mapping
    private static final Map<String, List<String>> MOOD_KEYWORDS = Map.of(
        "ENERGETIC", Arrays.asList("dancing", "party", "club", "energetic", "upbeat", "lively", "rave", "edm", "techno"),
        "CHILL", Arrays.asList("chill", "relax", "calm", "acoustic", "lounge", "jazz", "ambient", "peaceful"),
        "SOCIAL", Arrays.asList("social", "meet", "friends", "networking", "community", "gathering", "mixer"),
        "CULTURAL", Arrays.asList("cultural", "art", "museum", "gallery", "theater", "opera", "classical", "exhibition"),
        "ROMANTIC", Arrays.asList("romantic", "date", "intimate", "couple", "dinner", "wine", "candlelit"),
        "ADVENTUROUS", Arrays.asList("adventure", "outdoor", "hiking", "explore", "extreme", "sports", "active")
    );

    public MoodSearchResultDTO searchByMood(Long userId, MoodSearchRequest request) {
        String query = request.getQuery().toLowerCase();
        
        // Detect mood
        String detectedMood = detectMood(query);
        
        // Extract keywords
        List<String> keywords = extractKeywords(query, detectedMood);
        
        // Save search history
        if (userId != null) {
            saveMoodSearch(userId, request.getQuery(), detectedMood, keywords);
        }
        
        // Find matching events
        List<Event> events = findEventsByMood(detectedMood, keywords);
        
        // Build result
        MoodSearchResultDTO result = new MoodSearchResultDTO();
        result.setDetectedMood(detectedMood);
        result.setKeywords(keywords);
        result.setEvents(events.stream().map(this::convertEventToDTO).collect(Collectors.toList()));
        result.setInterpretation(generateInterpretation(detectedMood, keywords));
        
        return result;
    }

    private String detectMood(String query) {
        Map<String, Integer> moodScores = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : MOOD_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (query.contains(keyword)) {
                    score += 10;
                }
            }
            moodScores.put(entry.getKey(), score);
        }
        
        // Return mood with highest score
        return moodScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("SOCIAL");
    }

    private List<String> extractKeywords(String query, String mood) {
        List<String> keywords = new ArrayList<>();
        List<String> moodKeywords = MOOD_KEYWORDS.get(mood);
        
        for (String keyword : moodKeywords) {
            if (query.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        return keywords;
    }

    private List<Event> findEventsByMood(String mood, List<String> keywords) {
        List<Event> allEvents = eventRepository.findAll();
        
        return allEvents.stream()
            .filter(e -> e.getStartTime() != null && e.getStartTime().isAfter(LocalDateTime.now()))
            .filter(e -> matchesMood(e, mood, keywords))
            .sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()))
            .limit(20)
            .collect(Collectors.toList());
    }

    private boolean matchesMood(Event event, String mood, List<String> keywords) {
        String searchText = (event.getName() + " " + event.getDescription() + " " + 
                           (event.getMoodTags() != null ? event.getMoodTags() : "")).toLowerCase();
        
        // Check mood tags
        if (event.getMoodTags() != null && event.getMoodTags().toLowerCase().contains(mood.toLowerCase())) {
            return true;
        }
        
        // Check keywords
        for (String keyword : keywords) {
            if (searchText.contains(keyword)) {
                return true;
            }
        }
        
        // Check category
        if (event.getCategory() != null) {
            String categoryName = event.getCategory().getName().toLowerCase();
            for (String keyword : keywords) {
                if (categoryName.contains(keyword)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private void saveMoodSearch(Long userId, String query, String mood, List<String> keywords) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        
        MoodSearch search = new MoodSearch();
        search.setUser(user);
        search.setQuery(query);
        search.setDetectedMood(mood);
        search.setExtractedKeywords(String.join(",", keywords));
        search.setSearchedAt(LocalDateTime.now());
        
        moodSearchRepository.save(search);
    }

    private String generateInterpretation(String mood, List<String> keywords) {
        switch (mood) {
            case "ENERGETIC":
                return "Looking for high-energy events to dance and party!";
            case "CHILL":
                return "Seeking relaxed, laid-back experiences";
            case "SOCIAL":
                return "Want to meet new people and socialize";
            case "CULTURAL":
                return "Interested in arts, culture, and exhibitions";
            case "ROMANTIC":
                return "Planning a romantic evening out";
            case "ADVENTUROUS":
                return "Ready for outdoor adventures and activities";
            default:
                return "Exploring events in Tirana";
        }
    }

    private EventDTO convertEventToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setImageUrl(event.getImageUrl());
        dto.setStartTime(event.getStartTime());
        dto.setVenue(event.getVenue());
        dto.setPrice(event.getPrice());
        return dto;
    }
}
