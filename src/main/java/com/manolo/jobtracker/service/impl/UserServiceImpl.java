package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.request.UserRoleUpdateDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.mapper.UserMapper;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.UserNotFoundException;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.enums.ErrorCode;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {

        log.debug("Richiesta lista paginata User: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
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

    @Override
    public UserResponseDto updateRole(Long id, UserRoleUpdateDto dto) {

        log.debug("Aggiornamento ruolo User id={} a role={}", id, dto.getRole());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User non trovato con id: " + id
                ));

        user.setRole(dto.getRole());
        user = userRepository.save(user);

        log.info("Ruolo aggiornato con successo: id={}, nuovoRole={}", user.getId(), user.getRole());

        return UserMapper.toResponse(user);
    }
}