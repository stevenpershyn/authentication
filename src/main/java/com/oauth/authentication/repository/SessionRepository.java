package com.oauth.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.oauth.authentication.model.Session;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
	
	@Query("SELECT s FROM Session s WHERE s.sessionId = :sessionId")
    Optional<Session> findBySessionId(String sessionId);
}
