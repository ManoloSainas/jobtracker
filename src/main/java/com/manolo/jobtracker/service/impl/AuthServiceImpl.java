package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;
import com.manolo.jobtracker.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {

        log.info("Tentativo di login per email={}", dto.getEmail());

        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        log.info("Login effettuato correttamente per email={}", dto.getEmail());

        return new LoginResponseDto("test-token");
    }
}