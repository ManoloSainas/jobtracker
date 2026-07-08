package com.manolo.jobtracker.exception;

import com.manolo.jobtracker.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        description = "Struttura standard utilizzata per le risposte di errore dell'API"
)
public record ApiErrorResponseDTO(

        @Schema(
                description = "Data e ora in cui si è verificato l'errore",
                example = "2026-07-08T12:30:00"
        )
        LocalDateTime timestamp,


        @Schema(
                description = "Codice HTTP della risposta",
                example = "404"
        )
        int status,


        @Schema(
                description = "Codice identificativo dell'errore",
                example = "USER_NOT_FOUND"
        )
        ErrorCode error,


        @Schema(
                description = "Messaggio descrittivo dell'errore",
                example = "Utente non trovato con id: 10"
        )
        String message,


        @Schema(
                description = "Percorso della richiesta che ha generato l'errore",
                example = "/api/users/10"
        )
        String path,


        @Schema(
                description = "Lista degli errori di validazione dei singoli campi"
        )
        List<ApiValidationError> fieldErrors

) {}