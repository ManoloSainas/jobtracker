package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Dati necessari per la creazione di un nuovo utente"
)
public class UserRequestDto {

    @Schema(
            description = "Indirizzo email dell'utente",
            example = "mario.rossi@email.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @Schema(
            description = "Password dell'utente",
            example = "Password123!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3
    )
    @NotBlank(message = "Password obbligatoria")
    @Size(min = 3, max = 100, message = "Password tra 3 e 100 caratteri")
    private String password;

    @NotNull(message = "Role obbligatorio")
    private Role role;
}