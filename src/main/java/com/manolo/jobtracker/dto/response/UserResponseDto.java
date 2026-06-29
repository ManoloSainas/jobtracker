package com.manolo.jobtracker.dto.response;

import com.manolo.jobtracker.model.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private Role role;
}