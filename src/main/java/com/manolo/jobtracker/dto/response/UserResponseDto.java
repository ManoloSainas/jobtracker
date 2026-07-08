package com.manolo.jobtracker.dto.response;

import com.manolo.jobtracker.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Dati restituiti per un utente"
)
public class UserResponseDto {

    @Schema(
            description = "Identificativo univoco dell'utente",
            example = "1"
    )
    private Long id;


    @Schema(
            description = "Indirizzo email dell'utente",
            example = "mario.rossi@email.com"
    )
    private String email;


    @Schema(
            description = "Ruolo assegnato all'utente",
            example = "USER"
    )
    private Role role;
}