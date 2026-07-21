package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.exception.RefreshTokenException;
import com.manolo.jobtracker.model.RefreshToken;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    RefreshTokenServiceImpl refreshTokenService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(2L);
        user.setEmail("u@example.com");
    }

    @Test
    void createRefreshToken_saves_token_with_expiry_and_user() {
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        RefreshToken saved = RefreshToken.builder().id(1L).token("t").user(user).expiryDate(Instant.now().plusSeconds(3600)).build();
        when(refreshTokenRepository.save(any())).thenReturn(saved);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken captured = captor.getValue();
        assertThat(captured.getUser()).isEqualTo(user);
        assertThat(captured.getToken()).isNotNull();
        assertThat(captured.getExpiryDate()).isAfter(Instant.now());

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void verifyExpiration_when_expired_deletes_and_throws() {
        RefreshToken expired = RefreshToken.builder().id(1L).token("t").user(user).expiryDate(Instant.now().minusSeconds(10)).build();
        doNothing().when(refreshTokenRepository).delete(expired);

        assertThrows(RefreshTokenException.class, () -> refreshTokenService.verifyExpiration(expired));

        verify(refreshTokenRepository).delete(expired);
    }

    @Test
    void verifyExpiration_when_valid_returns_token() {
        RefreshToken valid = RefreshToken.builder().token("t").user(user).expiryDate(Instant.now().plusSeconds(3600)).build();
        RefreshToken res = refreshTokenService.verifyExpiration(valid);
        assertThat(res).isEqualTo(valid);
    }

    @Test
    void findByToken_when_missing_throws() {
        when(refreshTokenRepository.findByToken("x")).thenReturn(Optional.empty());
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.findByToken("x"));
    }

    @Test
    void deleteByToken_calls_repository() {
        doNothing().when(refreshTokenRepository).deleteByToken("x");
        refreshTokenService.deleteByToken("x");
        verify(refreshTokenRepository).deleteByToken("x");
    }

    @Test
    void revokeToken_sets_revoked_and_saves() {
        RefreshToken t = RefreshToken.builder().token("x").user(user).expiryDate(Instant.now().plusSeconds(100)).build();
        when(refreshTokenRepository.findByToken("x")).thenReturn(Optional.of(t));
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        refreshTokenService.revokeToken("x");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();
        assertThat(saved.isRevoked()).isTrue();
        assertThat(saved.getRevokedAt()).isNotNull();
    }

    @Test
    void revokeAllUserTokens_marks_all_and_savesAll() {
        RefreshToken t1 = RefreshToken.builder().token("a").user(user).build();
        RefreshToken t2 = RefreshToken.builder().token("b").user(user).build();
        when(refreshTokenRepository.findAllByUserId(user.getId())).thenReturn(List.of(t1, t2));

        when(refreshTokenRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        refreshTokenService.revokeAllUserTokens(user.getId());

        ArgumentCaptor<List<RefreshToken>> captor = ArgumentCaptor.forClass(List.class);
        verify(refreshTokenRepository).saveAll(captor.capture());
        List<RefreshToken> saved = captor.getValue();
        assertThat(saved).allMatch(RefreshToken::isRevoked);
        assertThat(saved).allMatch(rt -> rt.getRevokedAt() != null);
    }
}
