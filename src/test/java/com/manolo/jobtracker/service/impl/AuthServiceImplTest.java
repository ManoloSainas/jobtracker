package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;
import com.manolo.jobtracker.dto.response.LogoutResponseDto;
import com.manolo.jobtracker.exception.RefreshTokenException;
import com.manolo.jobtracker.model.RefreshToken;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.security.CustomUserDetails;
import com.manolo.jobtracker.security.jwt.JwtService;
import com.manolo.jobtracker.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @Mock
    RefreshTokenService refreshTokenService;

    @InjectMocks
    AuthServiceImpl authService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void login_success_generates_tokens() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("pwd");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any())).thenReturn("access-token");

        RefreshToken created = RefreshToken.builder().token("refresh-token").user(user).expiryDate(Instant.now().plusSeconds(3600)).build();
        when(refreshTokenService.createRefreshToken(any())).thenReturn(created);

        LoginResponseDto response = authService.login(dto);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(any());
        verify(refreshTokenService).createRefreshToken(any());
    }

    @Test
    void refreshToken_revoked_throws_and_revokes_all() {
        RefreshToken stored = RefreshToken.builder().token("old").user(user).revoked(true).build();
        when(refreshTokenService.findByToken("old")).thenReturn(stored);

        RefreshTokenException ex = assertThrows(RefreshTokenException.class, () -> authService.refreshToken("old"));
        assertThat(ex.getMessage()).contains("revoked");

        verify(refreshTokenService).revokeAllUserTokens(user.getId());
    }

    @Test
    void refreshToken_success_rotates() {
        RefreshToken stored = RefreshToken.builder().token("old").user(user).revoked(false).expiryDate(Instant.now().plusSeconds(3600)).build();
        when(refreshTokenService.findByToken("old")).thenReturn(stored);
        when(refreshTokenService.verifyExpiration(stored)).thenReturn(stored);

        doNothing().when(refreshTokenService).revokeToken("old");

        RefreshToken newToken = RefreshToken.builder().token("new").user(user).expiryDate(Instant.now().plusSeconds(3600)).build();
        when(refreshTokenService.createRefreshToken(user)).thenReturn(newToken);

        when(jwtService.generateToken(any())).thenReturn("new-access");

        LoginResponseDto resp = authService.refreshToken("old");

        assertThat(resp.getAccessToken()).isEqualTo("new-access");
        assertThat(resp.getRefreshToken()).isEqualTo("new");

        verify(refreshTokenService).revokeToken("old");
        verify(refreshTokenService).createRefreshToken(user);
        verify(jwtService).generateToken(any());
    }

    @Test
    void logout_calls_revoke_and_returns_message() {
        doNothing().when(refreshTokenService).revokeToken("t");

        LogoutResponseDto resp = authService.logout("t");

        assertThat(resp.message()).contains("Logout effettuato");
        verify(refreshTokenService).revokeToken("t");
    }
}
