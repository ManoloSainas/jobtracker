package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByUserIdAndStatus(Long userId, Status status);

    List<JobApplication> findByUserIdOrderByApplicationDateDesc(Long userId);

    boolean existsByUserIdAndCompanyAndPosition(Long userId, String company, String position);
}
