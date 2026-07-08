package com.manolo.jobtracker.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Errore relativo alla validazione di un singolo campo"
)
public record ApiValidationError(

        @Schema(
                description = "Nome del campo che ha generato l'errore",
                example = "email"
        )
        String field,


        @Schema(
                description = "Messaggio relativo all'errore di validazione",
                example = "Email non valida"
        )
        String message

) {}