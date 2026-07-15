package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(
        description = "Dati utilizzati per l'aggiornamento parziale di una candidatura"
)
public class JobApplicationPatchDto {

    @Schema(
            description = "Nuovo stato della candidatura",
            example = "INTERVIEW",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Status status;


    @Schema(
            description = "Lista degli identificativi dei tag da associare alla candidatura",
            example = "[1, 2]"
    )
    private List<Long> tagsIds;
}