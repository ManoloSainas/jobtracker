package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}
