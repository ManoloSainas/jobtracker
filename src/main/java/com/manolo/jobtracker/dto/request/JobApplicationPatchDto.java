package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobApplicationPatchDto {

    @NotNull
    private Status status;

    private List<Long> tagsIds;
}