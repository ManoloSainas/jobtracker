package com.manolo.jobtracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    private String password;
}