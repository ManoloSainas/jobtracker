package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 3, max = 100, message = "Password tra 3 e 100 caratteri")
    private String password;

    @NotNull(message = "Role obbligatorio")
    private Role role;
}