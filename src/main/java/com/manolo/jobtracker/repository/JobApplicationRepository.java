package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.enums.Status;
import com.manolo.jobtracker.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    Page<JobApplication> findByUserIdAndStatus(
            Long userId,
            Status status,
            Pageable pageable
    );

    boolean existsByUserIdAndCompanyAndPosition(
            Long userId,
            String company,
            String position
    );
}