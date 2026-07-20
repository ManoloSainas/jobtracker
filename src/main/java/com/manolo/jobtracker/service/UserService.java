package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.ChangePasswordRequestDto;
import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.request.UserRoleUpdateDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto createUser(UserRequestDto dto);

    UserResponseDto getUserById(Long id);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    void deleteUser(Long id);

    UserResponseDto updateRole(Long id, UserRoleUpdateDto dto);

    void changePassword(Long id, ChangePasswordRequestDto dto);
}