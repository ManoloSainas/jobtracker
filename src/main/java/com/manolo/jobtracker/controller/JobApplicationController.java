package com.manolo.jobtracker.controller;

import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public JobApplicationResponseDto create(@Valid @RequestBody JobApplicationRequestDto dto) {
        return service.createApplication(dto);
    }

    @GetMapping
    public List<JobApplicationResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public JobApplicationResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/user/{userId}")
    public List<JobApplicationResponseDto> getByUser(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @PutMapping("/{id}")
    public JobApplicationResponseDto update(
            @PathVariable Long id,
            @RequestBody JobApplicationRequestDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}