package com.manolo.jobtracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Dati restituiti per un tag"
)
public class TagResponseDto {

    @Schema(
            description = "Identificativo univoco del tag",
            example = "1"
    )
    private Long id;


    @Schema(
            description = "Nome del tag",
            example = "Java"
    )
    private String name;
}