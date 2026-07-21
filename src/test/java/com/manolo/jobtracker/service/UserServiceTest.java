package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.ChangePasswordRequestDto;
import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.InvalidPasswordException;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.security.PasswordValidator;
import com.manolo.jobtracker.service.impl.UserServiceImpl;
import com.manolo.jobtracker.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {


    private UserServiceImpl userService;


    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private RefreshTokenService refreshTokenService;


    @Mock
    private PasswordValidator passwordValidator;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);


        userService = new UserServiceImpl(
                userRepository,
                passwordEncoder,
                refreshTokenService,
                passwordValidator
        );

    }



    @Test
    void createUser_shouldCreateUser(){


        UserRequestDto dto =
                new UserRequestDto(
                        "test@test.com",
                        "Password1!"
                );


        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());


        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encoded-password");


        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setEmail(dto.getEmail());
        savedUser.setPassword("encoded-password");


        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);



        UserResponseDto response =
                userService.createUser(dto);



        assertNotNull(response);
        assertEquals(
                "test@test.com",
                response.getEmail()
        );


        verify(userRepository)
                .save(any(User.class));

    }




    @Test
    void createUser_shouldThrowExceptionIfEmailExists(){


        User user = new User();

        user.setEmail("test@test.com");


        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));



        UserRequestDto dto =
                new UserRequestDto(
                        "test@test.com",
                        "Password1!"
                );



        assertThrows(
                ConflictException.class,
                () -> userService.createUser(dto)
        );


        verify(userRepository, never())
                .save(any());

    }

    @Test
    void changePassword_shouldFail_whenOldPasswordWrong(){


        User user = new User();

        user.setId(1L);
        user.setPassword("encoded-old-password");


        ChangePasswordRequestDto dto =
                new ChangePasswordRequestDto(
                        "WrongPassword1!",
                        "NewPassword1!"
                );


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        when(passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPassword()
        ))
                .thenReturn(false);



        assertThrows(
                InvalidPasswordException.class,
                () -> userService.changePassword(1L, dto)
        );


        verify(refreshTokenService, never())
                .revokeAllUserTokens(anyLong());
    }

    @Test
    void changePassword_shouldChangePasswordSuccessfully(){


        User user = new User();

        user.setId(1L);
        user.setPassword("encoded-old-password");


        ChangePasswordRequestDto dto =
                new ChangePasswordRequestDto(
                        "OldPassword1!",
                        "NewPassword1!"
                );


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));


        when(passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPassword()
        ))
                .thenReturn(true);


        when(passwordEncoder.encode(
                dto.getNewPassword()
        ))
                .thenReturn("encoded-new-password");



        userService.changePassword(1L, dto);



        assertEquals(
                "encoded-new-password",
                user.getPassword()
        );


        verify(userRepository)
                .save(user);


        verify(refreshTokenService)
                .revokeAllUserTokens(1L);
    }

}