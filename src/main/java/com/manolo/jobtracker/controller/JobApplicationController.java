package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.exception.ApiErrorResponseDTO;
import com.manolo.jobtracker.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(
        name = "Candidature",
        description = "Operazioni per la gestione delle candidature"
)
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }


    @PostMapping
    @Operation(
            summary = "Crea una nuova candidatura",
            description = "Crea una nuova candidatura lavorativa associata a un utente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Candidatura creata correttamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati della richiesta non validi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utente non trovato",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Candidatura già esistente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public JobApplicationResponseDto create(
            @Valid @RequestBody JobApplicationRequestDto dto
    ) {
        return service.createApplication(dto);
    }


    @GetMapping
    @Operation(
            summary = "Recupera tutte le candidature",
            description = "Restituisce la lista di tutte le candidature presenti nel sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista candidature recuperata correttamente"
    )
    public List<JobApplicationResponseDto> getAll() {
        return service.getAll();
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Recupera una candidatura tramite ID",
            description = "Restituisce una singola candidatura identificata dal suo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidatura trovata correttamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidatura non trovata",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public JobApplicationResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Recupera le candidature di un utente",
            description = "Restituisce tutte le candidature associate a uno specifico utente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidature recuperate correttamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Utente non trovato",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public List<JobApplicationResponseDto> getByUser(
            @PathVariable Long userId
    ) {
        return service.getByUserId(userId);
    }


    @PatchMapping("/{id}")
    @Operation(
            summary = "Aggiorna una candidatura",
            description = "Aggiorna parzialmente i dati di una candidatura esistente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidatura aggiornata correttamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dati della richiesta non validi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidatura non trovata",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public ResponseEntity<JobApplicationResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid JobApplicationPatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(id, dto));
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimina una candidatura",
            description = "Elimina una candidatura dal sistema tramite il suo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Candidatura eliminata correttamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidatura non trovata",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}