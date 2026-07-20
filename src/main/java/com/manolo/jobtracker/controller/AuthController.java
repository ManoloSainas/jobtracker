package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.request.RefreshTokenRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;
import com.manolo.jobtracker.dto.response.LogoutResponseDto;
import com.manolo.jobtracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponseDto login(
            @Valid @RequestBody LoginRequestDto dto
    ) {

        return service.login(dto);
    }

    @PostMapping("/refresh")
    public LoginResponseDto refreshToken(
            @RequestBody RefreshTokenRequestDto dto
    ) {

        return service.refreshToken(dto.getRefreshToken());
    }

    @PostMapping("/logout")
    public LogoutResponseDto logout(
            @RequestBody RefreshTokenRequestDto dto
    ) {

        return service.logout(dto.getRefreshToken());
    }
}