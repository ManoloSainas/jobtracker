package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponseDto createApplication(JobApplicationRequestDto dto);

    JobApplicationResponseDto getById(Long id);

    Page<JobApplicationResponseDto> getAll(Pageable pageable);

    Page<JobApplicationResponseDto> getMyApplications(Pageable pageable);

    JobApplicationResponseDto patch(Long id, JobApplicationPatchDto dto);

    void delete(Long id);
}