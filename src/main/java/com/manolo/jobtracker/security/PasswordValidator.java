package com.manolo.jobtracker.security;

import com.manolo.jobtracker.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$"
            );


    public void validate(String password) {

        if(password == null ||
                !PASSWORD_PATTERN.matcher(password).matches()) {

            throw new InvalidPasswordException(
                    "La password deve contenere almeno 8 caratteri, una lettera maiuscola, una minuscola, un numero e un carattere speciale"
            );
        }
    }
}