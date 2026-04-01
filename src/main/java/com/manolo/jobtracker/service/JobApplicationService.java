
package com.manolo.jobtracker.service;

import com.manolo.jobtracker.model.JobApplication;

import java.util.List;

public interface JobApplicationService {

    JobApplication createApplication(JobApplication jobApplication);

    JobApplication getById(Long id);

    List<JobApplication> getAll();

    List<JobApplication> getByUserId(Long userId);

    JobApplication update(Long id, JobApplication jobApplication);

    void delete(Long id);
}