package com.tirana.events.config;

import com.tirana.events.model.Category;
import com.tirana.events.model.Event;
import com.tirana.events.model.User;
import com.tirana.events.repository.CategoryRepository;
import com.tirana.events.repository.EventRepository;
import com.tirana.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Only initialize data if database is empty
        if (categoryRepository.count() > 0) {
            System.out.println("Database already initialized. Skipping data initialization.");
            return;
        }
        
        System.out.println("Initializing database with demo data...");
        
        // Create categories
        Category music = createCategory("Music", "musical-notes", "#8B5CF6");
        Category university = createCategory("University", "school", "#3B82F6");
        Category culture = createCategory("Culture", "color-palette", "#EF4444");
        Category volunteering = createCategory("Volunteering", "people", "#10B981");
        Category more = createCategory("More", "ellipsis-horizontal", "#6B7280");
        
        // Create demo user
        User demoUser = new User();
        demoUser.setEmail("demo@tirana.com");
        demoUser.setPassword(passwordEncoder.encode("demo123"));
        demoUser.setFullName("Demo User");
        demoUser = userRepository.save(demoUser);
        
        // Create sample events
        createEvent("Sunset Festival 2024", 
                   "The biggest open-air music festival in the heart of Tirana. Featuring international DJs, live performances, food, and amazing vibes!",
                   "Sheshi Skënderbej, Tirana",
                   41.3275, 19.8187,
                   LocalDateTime.now().plusDays(10),
                   "https://images.unsplash.com/photo-1459749411175-04bf5292ceea",
                   music, demoUser, 5000);
        
        createEvent("Friday Night Party",
                   "Join us for an unforgettable night of music, dancing, and fun at Mulliri Vjeter!",
                   "Mulliri Vjeter, Tirana",
                   41.3275, 19.8187,
                   LocalDateTime.now().plusDays(5),
                   "https://images.unsplash.com/photo-1566737236500-c8ac43014a67",
                   music, demoUser, 200);
        
        createEvent("Techno Night",
                   "Experience the best techno music with top DJs from around Europe.",
                   "Folie Terrace, Tirana",
                   41.3275, 19.8187,
                   LocalDateTime.now().plusDays(15),
                   "https://images.unsplash.com/photo-1571266028243-d220c6e2e5e4",
                   music, demoUser, 300);
        
        createEvent("Stand Up Comedy Night",
                   "Laugh out loud with the best comedians in Albania!",
                   "Teatri Kombetar, Tirana",
                   41.3275, 19.8187,
                   LocalDateTime.now().plusDays(7),
                   "https://images.unsplash.com/photo-1585699324551-f6c309eedeca",
                   culture, demoUser, 150);
        
        createEvent("Beach Cleanup Volunteer",
                   "Help us keep our beaches clean! Join our volunteer team for a day of environmental action.",
                   "Durres Beach",
                   41.3231, 19.4565,
                   LocalDateTime.now().plusDays(12),
                   "https://images.unsplash.com/photo-1618477461853-cf6ed80faba5",
                   volunteering, demoUser, 100);
        
        System.out.println("Database initialization completed!");
    }
    
    private Category createCategory(String name, String icon, String color) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setColor(color);
        return categoryRepository.save(category);
    }
    
    private Event createEvent(String name, String description, String location,
                             Double lat, Double lng, LocalDateTime startDate,
                             String imageUrl, Category category, User organizer,
                             Integer maxAttendees) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setLocation(location);
        event.setLatitude(lat);
        event.setLongitude(lng);
        event.setStartDate(startDate);
        event.setEndDate(startDate.plusHours(4));
        event.setImageUrl(imageUrl);
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setMaxAttendees(maxAttendees);
        return eventRepository.save(event);
    }
}
