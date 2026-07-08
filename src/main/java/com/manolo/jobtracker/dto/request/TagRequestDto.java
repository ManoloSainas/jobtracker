package com.manolo.jobtracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    private String name;
}