package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @PostMapping
    public TagResponseDto create(@RequestBody TagRequestDto dto) {
        return service.createTag(dto);
    }

    @GetMapping
    public List<TagResponseDto> getAll() {
        return service.getAllTags();
    }

    @GetMapping("/{id}")
    public TagResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }
}