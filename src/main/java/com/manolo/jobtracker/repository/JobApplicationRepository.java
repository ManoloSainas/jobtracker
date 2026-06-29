package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByUserIdAndStatus(Long userId, Status status);

    List<JobApplication> findByUserIdOrderByApplicationDateDesc(Long userId);
}
