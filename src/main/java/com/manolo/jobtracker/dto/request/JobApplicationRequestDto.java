package com.manolo.jobtracker.dto.request;

import com.manolo.jobtracker.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotNull(message = "Status obbligatorio")
    private Status status;

    @NotBlank(message = "Company obbligatoria")
    private String company;

    @NotBlank(message = "Position obbligatoria")
    private String position;

    @NotNull(message = "Data applicazione obbligatoria")
    private LocalDate applicationDate;

    @NotNull(message = "UserId obbligatorio")
    private Long userId;

    @NotEmpty(message = "Devi inserire almeno un tag")
    private List<Long> tagsIds;
}
