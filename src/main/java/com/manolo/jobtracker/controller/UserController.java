package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.UserRequestDto;
import com.manolo.jobtracker.dto.response.UserResponseDto;
import com.manolo.jobtracker.exception.ApiErrorResponseDTO;
import com.manolo.jobtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Utenti",
        description = "Operazioni per la gestione degli utenti"
)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crea un nuovo utente",
            description = "Crea un nuovo utente nel sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Utente creato correttamente"
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
                    responseCode = "409",
                    description = "Email già esistente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public UserResponseDto create(@Valid @RequestBody UserRequestDto dto) {
        return service.createUser(dto);
    }


    @GetMapping
    @Operation(
            summary = "Recupera tutti gli utenti",
            description = "Restituisce la lista di tutti gli utenti presenti nel sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista utenti recuperata correttamente"
    )
    public List<UserResponseDto> getAll() {
        return service.getAllUsers();
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Recupera un utente tramite ID",
            description = "Restituisce un singolo utente identificato dal suo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Utente trovato correttamente"
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
    public UserResponseDto getById(@PathVariable Long id) {
        return service.getUserById(id);
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimina un utente",
            description = "Elimina un utente dal sistema tramite il suo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Utente eliminato correttamente"
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
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}