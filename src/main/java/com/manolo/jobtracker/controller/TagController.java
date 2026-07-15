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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
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
            description = "Restituisce i tag presenti nel sistema con paginazione."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista dei tag recuperata correttamente"
    )
    public Page<TagResponseDto> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return service.getAllTags(pageable);
    }
}