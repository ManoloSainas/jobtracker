package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;
import com.manolo.jobtracker.dto.response.LogoutResponseDto;
import com.manolo.jobtracker.exception.RefreshTokenException;
import com.manolo.jobtracker.model.RefreshToken;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.security.CustomUserDetails;
import com.manolo.jobtracker.security.jwt.JwtService;
import com.manolo.jobtracker.service.AuthService;
import com.manolo.jobtracker.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {

        log.info("Tentativo di login per email={}", dto.getEmail());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        log.info("Login effettuato correttamente per email={}", dto.getEmail());

        assert userDetails != null;

        String accessToken =
                jwtService.generateToken(userDetails);

        User user =
                ((CustomUserDetails) userDetails).getUser();

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponseDto(
                accessToken,
                refreshToken.getToken()
        );
    }

    @Override
    public LoginResponseDto refreshToken(String refreshToken) {

        log.info("Richiesta refresh token");

        RefreshToken storedToken =
                refreshTokenService.findByToken(refreshToken);

        if (storedToken.isRevoked()) {

            log.warn(
                    "Tentativo di utilizzo di refresh token revocato per userId={}",
                    storedToken.getUser().getId()
            );

            refreshTokenService.revokeAllUserTokens(
                    storedToken.getUser().getId()
            );

            throw new RefreshTokenException(
                    "Refresh token revoked"
            );
        }

        refreshTokenService.verifyExpiration(storedToken);

        User user = storedToken.getUser();

        refreshTokenService.revokeToken(refreshToken);

        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String newAccessToken =
                jwtService.generateToken(userDetails);

        log.info("Refresh token ruotato per email={}",
                user.getEmail());

        return new LoginResponseDto(
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    @Override
    public LogoutResponseDto logout(String refreshToken) {

        log.info("Richiesta logout");

        refreshTokenService.revokeToken(refreshToken);

        log.info("Logout effettuato correttamente");

        return new LogoutResponseDto(
                "Logout effettuato correttamente"
        );
    }
}