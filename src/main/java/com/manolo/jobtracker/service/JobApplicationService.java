package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponseDto createApplication(JobApplicationRequestDto dto);

    JobApplicationResponseDto getById(Long id);

    List<JobApplicationResponseDto> getAll();

    List<JobApplicationResponseDto> getByUserId(Long userId);

    JobApplicationResponseDto patch(Long id, JobApplicationPatchDto dto);

    void delete(Long id);
}