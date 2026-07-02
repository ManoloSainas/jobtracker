package com.manolo.jobtracker.mapper;

import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.model.JobApplication;

import java.util.stream.Collectors;

public class JobApplicationMapper {

    public static JobApplicationResponseDto toResponse(JobApplication jobApplication) {

        return JobApplicationResponseDto.builder()
                .id(jobApplication.getId())
                .status(jobApplication.getStatus())
                .company(jobApplication.getCompany())
                .position(jobApplication.getPosition())
                .applicationDate(jobApplication.getApplicationDate())
                .user(UserMapper.toResponse(jobApplication.getUser()))
                .tags(jobApplication.getTags()
                        .stream()
                        .map(TagMapper::toResponse)
                        .collect(Collectors.toSet()))
                .build();
    }
}