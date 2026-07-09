package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.mapper.UserMapper;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.UserNotFoundException;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.enums.ErrorCode;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {

        log.debug("Creazione User avviata: email={}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {

            throw new ConflictException(
                    "Email già registrata: " + dto.getEmail(),
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);

        log.info("User creato con successo: id={}, email={}", user.getId(), user.getEmail());

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        log.debug("Richiesta User per id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User non trovato con id: " + id
                ));

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {

        log.debug("Richiesta lista completa User");

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {

        log.debug("Richiesta eliminazione User id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User non trovato con id: " + id
                ));

        userRepository.delete(user);

        log.info("User eliminato con successo: id={}", id);
    }
}