package com.oauth.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.authentication.model.Session;
import com.oauth.authentication.repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    // Save a new session after authentication
    public Session createSession(Integer userId) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());  // Generate unique session ID
        session.setUserId(userId);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusHours(1));  // Set expiration time as needed

        return sessionRepository.save(session);
    }

    // Find session by session ID
    public Session findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId).orElse(null);
    }

    // Delete session (optional, for logout functionality)
    public void deleteSession(String sessionId) {
        sessionRepository.findBySessionId(sessionId)
            .ifPresent(sessionRepository::delete);
    }
}
