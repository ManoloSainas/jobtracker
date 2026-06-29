package com.manolo.jobtracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {

    @NotBlank
    private String name;
}