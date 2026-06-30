package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.dto.mapper.UserMapper;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.UserNotFoundException;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.model.enums.ErrorCode;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email già registrata: " + dto.getEmail(),
                    ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = UserMapper.toEntity(dto);

        user = userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User non trovato con id: " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User non trovato con id: " + id));

        userRepository.delete(user);
    }
}