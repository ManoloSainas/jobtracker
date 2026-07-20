package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpiryDateBefore(Instant now);

    List<RefreshToken> findAllByUserId(Long userId);

    List<RefreshToken> findAllByRevokedTrue();
}