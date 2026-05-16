package com.tirana.events.service;

import com.tirana.events.dto.CreateReviewRequest;
import com.tirana.events.dto.EventReviewDTO;
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
public class ReviewService {
    private final EventReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final LoyaltyService loyaltyService;

    @Transactional
    public EventReviewDTO createReview(Long userId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if user already reviewed
        if (reviewRepository.existsByUserIdAndEventId(userId, request.getEventId())) {
            throw new RuntimeException("You have already reviewed this event");
        }

        // Check if user attended (has ticket)
        boolean hasTicket = ticketRepository.existsByUserIdAndEventId(userId, request.getEventId());

        EventReview review = new EventReview();
        review.setUser(user);
        review.setEvent(event);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setVibeTags(request.getVibeTags());
        review.setIsVerifiedAttendee(hasTicket);
        review.setCreatedAt(LocalDateTime.now());

        review = reviewRepository.save(review);

        // Award loyalty points for review
        if (hasTicket) {
            loyaltyService.awardPoints(userId, 50, "REVIEW", 
                "Reviewed " + event.getName(), request.getEventId());
        }

        return convertToDTO(review);
    }

    public List<EventReviewDTO> getEventReviews(Long eventId) {
        return reviewRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventReviewDTO> getVerifiedReviews(Long eventId) {
        return reviewRepository.findVerifiedReviewsByEventId(eventId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long eventId) {
        Double avg = reviewRepository.getAverageRating(eventId);
        return avg != null ? avg : 0.0;
    }

    @Transactional
    public void addOrganizerReply(Long reviewId, Long organizerId, String reply) {
        EventReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Verify organizer owns the event
        if (!review.getEvent().getOrganizer().getId().equals(organizerId)) {
            throw new RuntimeException("Only event organizer can reply");
        }

        review.setOrganizerReply(reply);
        review.setOrganizerRepliedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Transactional
    public void markHelpful(Long reviewId) {
        EventReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    private EventReviewDTO convertToDTO(EventReview review) {
        EventReviewDTO dto = new EventReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setUserAvatar(review.getUser().getProfilePicture());
        dto.setEventId(review.getEvent().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setVibeTags(review.getVibeTags());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setIsVerifiedAttendee(review.getIsVerifiedAttendee());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setOrganizerReply(review.getOrganizerReply());
        dto.setOrganizerRepliedAt(review.getOrganizerRepliedAt());
        return dto;
    }
}
