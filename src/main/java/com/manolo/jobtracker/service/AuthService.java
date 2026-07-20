package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;
import com.manolo.jobtracker.dto.response.LogoutResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto dto);

    LoginResponseDto refreshToken(String refreshToken);

    LogoutResponseDto logout(String refreshToken);
}