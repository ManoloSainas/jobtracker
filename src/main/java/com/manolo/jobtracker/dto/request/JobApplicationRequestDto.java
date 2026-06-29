package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequestDto {

    @NotNull
    private Status status;

    @NotBlank
    private String company;

    @NotBlank
    private String position;

    @NotNull
    private LocalDate applicationDate;

    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1)
    private List<Long> tagsIds;
}
