package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.exception.ApiErrorResponseDTO;
import com.manolo.jobtracker.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@Tag(
        name = "Tag",
        description = "Operazioni per la gestione dei tag"
)
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }


    @PostMapping
    @Operation(
            summary = "Crea un nuovo tag",
            description = "Crea un nuovo tag associato al sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag creato correttamente"
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
                    description = "Tag già esistente",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public TagResponseDto create(@Valid @RequestBody TagRequestDto dto) {
        return service.createTag(dto);
    }


    @GetMapping
    @Operation(
            summary = "Recupera tutti i tag",
            description = "Restituisce la lista di tutti i tag presenti nel sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista dei tag recuperata correttamente"
    )
    public List<TagResponseDto> getAll() {
        return service.getAllTags();
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Recupera un tag tramite ID",
            description = "Restituisce un singolo tag identificato dal suo ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag trovato correttamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag non trovato",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponseDTO.class
                            )
                    )
            )
    })
    public TagResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }
}