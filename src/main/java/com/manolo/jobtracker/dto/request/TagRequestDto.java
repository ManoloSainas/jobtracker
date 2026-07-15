package com.manolo.jobtracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Dati necessari per la creazione di un nuovo tag"
)
public class TagRequestDto {


    @Schema(
            description = "Nome del tag",
            example = "Java",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Nome tag obbligatorio")
    @Size(
            max = 50,
            message = "Il nome del tag non può superare i 50 caratteri"
    )
    private String name;
}