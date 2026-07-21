package com.manolo.jobtracker.security;

import com.manolo.jobtracker.exception.InvalidPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    @DisplayName("valid password passes")
    void validPassword() {
        assertDoesNotThrow(() -> validator.validate("Abcdef1!"));
    }

    @Test
    @DisplayName("null or blank password throws")
    void nullOrBlank() {
        assertThrows(InvalidPasswordException.class, () -> validator.validate(null));
        assertThrows(InvalidPasswordException.class, () -> validator.validate(""));
    }

    @Test
    @DisplayName("too short password throws")
    void tooShort() {
        assertThrows(InvalidPasswordException.class, () -> validator.validate("A1!a"));
    }

    @Test
    @DisplayName("missing uppercase throws")
    void missingUppercase() {
        assertThrows(InvalidPasswordException.class, () -> validator.validate("abcdef1!"));
    }

    @Test
    @DisplayName("missing digit throws")
    void missingDigit() {
        assertThrows(InvalidPasswordException.class, () -> validator.validate("Abcdefg!"));
    }

    @Test
    @DisplayName("missing special char throws")
    void missingSpecial() {
        assertThrows(InvalidPasswordException.class, () -> validator.validate("Abcdef12"));
    }

    @Test
    @DisplayName("boundary length accepted when rules met")
    void boundaryAccepted() {
        assertDoesNotThrow(() -> validator.validate("A1b2c3!d"));
    }
}
