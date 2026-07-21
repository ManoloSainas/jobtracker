package com.manolo.jobtracker.security;

import com.manolo.jobtracker.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {


    private final PasswordValidator validator =
            new PasswordValidator();


    @Test
    void shouldAcceptValidPassword() {

        assertDoesNotThrow(() ->
                validator.validate("Password1!")
        );
    }


    @Test
    void shouldRejectPasswordWithoutUppercase() {

        assertThrows(
                InvalidPasswordException.class,
                () -> validator.validate("password1!")
        );
    }


    @Test
    void shouldRejectPasswordWithoutNumber() {

        assertThrows(
                InvalidPasswordException.class,
                () -> validator.validate("Password!")
        );
    }


    @Test
    void shouldRejectPasswordTooShort() {

        assertThrows(
                InvalidPasswordException.class,
                () -> validator.validate("Pa1!")
        );
    }


    @Test
    void shouldRejectNullPassword() {

        assertThrows(
                InvalidPasswordException.class,
                () -> validator.validate(null)
        );
    }
}