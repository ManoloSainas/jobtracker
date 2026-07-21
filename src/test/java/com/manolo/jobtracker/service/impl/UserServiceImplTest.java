package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.request.ChangePasswordRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.enums.Role;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.UserNotFoundException;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.manolo.jobtracker.security.PasswordValidator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    PasswordValidator passwordValidator;

    @InjectMocks
    UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setEmail("john@example.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("createUser: success saves and returns dto")
    void createUser_success() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("a@b.com");
        dto.setPassword("Password1!");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
                doNothing().when(passwordValidator).validate(anyString());

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(77L);
            return u;
        });

        UserResponseDto out = userService.createUser(dto);
        assertThat(out.getId()).isEqualTo(77L);
        assertThat(captor.getValue().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    @DisplayName("createUser: conflict when email exists")
    void createUser_conflict() {
        UserRequestDto dto = new UserRequestDto(); dto.setEmail("john@example.com"); dto.setPassword("x");
                when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));
        assertThrows(ConflictException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getUserById: not found throws")
    void getUserById_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    @DisplayName("changePassword: success revokes tokens and updates password")
    void changePassword_success() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("old"); dto.setNewPassword("newPass1!");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
                when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);
                doNothing().when(passwordValidator).validate(anyString());
                                when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodedNew");
                                when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                userService.changePassword(user.getId(), dto);

                verify(refreshTokenService).revokeAllUserTokens(user.getId());
                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
                verify(userRepository).save(captor.capture());
                assertThat(captor.getValue().getPassword()).isNotNull();
    }

    @Test
    @DisplayName("changePassword: wrong old password throws")
    void changePassword_wrongOld() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(); dto.setOldPassword("bad"); dto.setNewPassword("new");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
                when(passwordEncoder.matches("bad", user.getPassword())).thenReturn(false);
                assertThrows(com.manolo.jobtracker.exception.InvalidPasswordException.class, () -> userService.changePassword(user.getId(), dto));
        verify(refreshTokenService, never()).revokeAllUserTokens(anyLong());
    }

    @Test
    @DisplayName("getAllUsers: returns paged list")
    void getAllUsers_paged() {
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(user)));
        var page = userService.getAllUsers(PageRequest.of(0,10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteUser: not found throws")
    void deleteUser_notFound() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(123L));
    }
}
