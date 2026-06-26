package com.manolo.jobtracker.dto;

import java.util.List;

public class UserDTO {
    public Long id;
    public String email;
    public String role;
    public List<JobApplicationDTO> applications;
}
