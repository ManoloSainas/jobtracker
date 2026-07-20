package com.manolo.jobtracker.service;

public interface RefreshTokenCleanupService {

    void deleteExpiredTokens();
}