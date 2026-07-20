package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.repository.RefreshTokenRepository;
import com.manolo.jobtracker.service.RefreshTokenCleanupService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class RefreshTokenCleanupServiceImpl
        implements RefreshTokenCleanupService {


    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshTokenCleanupServiceImpl(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredTokens() {

        log.info("Pulizia refresh token scaduti iniziata");

        refreshTokenRepository
                .deleteAllByExpiryDateBefore(Instant.now());

        log.info("Pulizia refresh token scaduti completata");
    }
}