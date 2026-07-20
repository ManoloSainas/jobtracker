package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.exception.RefreshTokenException;
import com.manolo.jobtracker.model.RefreshToken;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.RefreshTokenRepository;
import com.manolo.jobtracker.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(
                        Instant.now()
                                .plusSeconds(
                                        60 * 60 * 24 * REFRESH_TOKEN_DURATION_DAYS
                                )
                )
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public RefreshToken verifyExpiration(
            RefreshToken refreshToken
    ) {

        if (refreshToken.getExpiryDate()
                .isBefore(Instant.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RefreshTokenException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }


    @Override
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RefreshTokenException(
                                "Refresh token not found"
                        )
                );
    }


    @Override
    @Transactional
    public void deleteByToken(String token) {

        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = findByToken(token);

        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {

        List<RefreshToken> tokens =
                refreshTokenRepository.findAllByUserId(userId);

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setRevokedAt(Instant.now());
        });

        refreshTokenRepository.saveAll(tokens);
    }
}