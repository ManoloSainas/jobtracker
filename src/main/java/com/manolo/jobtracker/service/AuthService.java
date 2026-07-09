package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.LoginRequestDto;
import com.manolo.jobtracker.dto.response.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto dto);
}