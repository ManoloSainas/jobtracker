package com.manolo.jobtracker.service;

import com.manolo.jobtracker.model.RefreshToken;
import com.manolo.jobtracker.model.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    void deleteByToken(String token);

    void revokeToken(String token);

    void revokeAllUserTokens(Long userId);
}