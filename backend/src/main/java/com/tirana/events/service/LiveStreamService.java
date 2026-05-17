package com.tirana.events.service;

import com.tirana.events.dto.LiveStreamDTO;
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
public class LiveStreamService {
    private final LiveStreamRepository streamRepository;
    private final EventRepository eventRepository;

    @Transactional
    public LiveStreamDTO startStream(Long eventId, String streamUrl) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));
        
        LiveStream stream = streamRepository.findByEventId(eventId)
            .orElse(new LiveStream());
        
        stream.setEvent(event);
        stream.setStreamUrl(streamUrl);
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        stream.setViewersCount(0);
        stream.setReactionsCount(0);
        
        stream = streamRepository.save(stream);
        
        return convertToDTO(stream);
    }

    @Transactional
    public void endStream(Long streamId) {
        LiveStream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found"));
        
        stream.setIsLive(false);
        stream.setEndedAt(LocalDateTime.now());
        streamRepository.save(stream);
    }

    public List<LiveStreamDTO> getActiveLiveStreams() {
        return streamRepository.findActiveLiveStreams()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void incrementViewers(Long streamId) {
        LiveStream stream = streamRepository.findById(streamId).orElse(null);
        if (stream != null) {
            stream.setViewersCount(stream.getViewersCount() + 1);
            streamRepository.save(stream);
        }
    }

    @Transactional
    public void addReaction(Long streamId) {
        LiveStream stream = streamRepository.findById(streamId).orElse(null);
        if (stream != null) {
            stream.setReactionsCount(stream.getReactionsCount() + 1);
            streamRepository.save(stream);
        }
    }

    private LiveStreamDTO convertToDTO(LiveStream stream) {
        LiveStreamDTO dto = new LiveStreamDTO();
        dto.setId(stream.getId());
        dto.setEventId(stream.getEvent().getId());
        dto.setEventName(stream.getEvent().getName());
        dto.setEventImageUrl(stream.getEvent().getImageUrl());
        dto.setStreamUrl(stream.getStreamUrl());
        dto.setThumbnailUrl(stream.getThumbnailUrl());
        dto.setIsLive(stream.getIsLive());
        dto.setViewersCount(stream.getViewersCount());
        dto.setReactionsCount(stream.getReactionsCount());
        dto.setStartedAt(stream.getStartedAt());
        return dto;
    }
}
