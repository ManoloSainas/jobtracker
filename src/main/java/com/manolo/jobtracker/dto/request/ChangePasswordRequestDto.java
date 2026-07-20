package com.manolo.jobtracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}