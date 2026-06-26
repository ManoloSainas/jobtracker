package com.manolo.jobtracker.dto;

import java.time.LocalDate;
import java.util.List;

public class JobApplicationDTO {
    public Long id;
    public String status;
    public String company;
    public String position;
    public LocalDate applicationDate;
    public List<TagDTO> tags;
}
