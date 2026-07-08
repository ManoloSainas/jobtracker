package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Dati necessari per la creazione di una nuova candidatura"
)
public class JobApplicationRequestDto {

    @Schema(
            description = "Stato attuale della candidatura",
            example = "APPLIED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Status obbligatorio")
    private Status status;

    @Schema(
            description = "Nome dell'azienda presso cui è stata inviata la candidatura",
            example = "Google",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Company obbligatoria")
    private String company;

    @Schema(
            description = "Posizione lavorativa per cui ci si è candidati",
            example = "Backend Developer",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Position obbligatoria")
    private String position;

    @Schema(
            description = "Data di invio della candidatura",
            example = "2026-07-08",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Data applicazione obbligatoria")
    private LocalDate applicationDate;

    @Schema(
            description = "Identificativo dell'utente associato alla candidatura",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "UserId obbligatorio")
    private Long userId;

    @Schema(
            description = "Lista degli identificativi dei tag associati alla candidatura",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Devi inserire almeno un tag")
    private List<Long> tagsIds;
}
