package com.manolo.jobtracker.dto.response;

import com.manolo.jobtracker.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Dati restituiti per una candidatura"
)
public class JobApplicationResponseDto {

    @Schema(
            description = "Identificativo univoco della candidatura",
            example = "1"
    )
    private Long id;


    @Schema(
            description = "Stato della candidatura",
            example = "INTERVIEW"
    )
    private Status status;


    @Schema(
            description = "Nome dell'azienda",
            example = "Google"
    )
    private String company;


    @Schema(
            description = "Posizione lavorativa",
            example = "Backend Developer"
    )
    private String position;


    @Schema(
            description = "Data di invio della candidatura",
            example = "2026-07-08"
    )
    private LocalDate applicationDate;


    @Schema(
            description = "Utente associato alla candidatura"
    )
    private UserResponseDto user;


    @Schema(
            description = "Tag associati alla candidatura"
    )
    private Set<TagResponseDto> tags;
}