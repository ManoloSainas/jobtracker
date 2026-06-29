package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto dto);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    void deleteUser(Long id);
}