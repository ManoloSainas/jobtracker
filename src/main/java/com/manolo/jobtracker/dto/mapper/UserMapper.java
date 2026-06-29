package com.manolo.jobtracker.dto.mapper;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.model.User;

public class UserMapper {

    public static User toEntity(UserRequestDto dto) {

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        return user;
    }

    public static UserResponseDto toResponse(User user) {

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}