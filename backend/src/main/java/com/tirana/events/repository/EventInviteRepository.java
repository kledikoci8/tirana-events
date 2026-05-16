package com.tirana.events.repository;

import com.tirana.events.model.EventInvite;
import com.tirana.events.model.User;
import com.tirana.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventInviteRepository extends JpaRepository<EventInvite, Long> {
    
    Optional<EventInvite> findByInviteToken(String inviteToken);
    
    List<EventInvite> findByInviter(User inviter);
    
    List<EventInvite> findByInvitee(User invitee);
    
    List<EventInvite> findByEvent(Event event);
    
    List<EventInvite> findByEventAndInviter(Event event, User inviter);
}
