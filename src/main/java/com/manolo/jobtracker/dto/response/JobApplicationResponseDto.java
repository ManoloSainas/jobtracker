package com.manolo.jobtracker.dto.response;

import com.manolo.jobtracker.enums.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationResponseDto {

    private Long id;
    private Status status;
    private String company;
    private String position;
    private LocalDate applicationDate;
    private UserResponseDto user;
    private Set<TagResponseDto> tags;
}