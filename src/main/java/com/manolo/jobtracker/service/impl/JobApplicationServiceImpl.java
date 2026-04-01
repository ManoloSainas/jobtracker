package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.repository.JobApplicationRepository;
import com.manolo.jobtracker.service.JobApplicationService;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public JobApplication createApplication(JobApplication jobApplication) {
        return jobApplicationRepository.save(jobApplication);
    }

    @Override
    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id).orElse(null);
    }

    @Override
    public List<JobApplication> getAll() {
        return jobApplicationRepository.findAll();
    }

    @Override
    public List<JobApplication> getByUserId(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    @Override
    public JobApplication update(Long id, JobApplication jobApplication) {
        JobApplication existing = jobApplicationRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setCompany(jobApplication.getCompany());
            existing.setPosition(jobApplication.getPosition());
            existing.setStatus(jobApplication.getStatus());
            return jobApplicationRepository.save(existing);
        }

        return null;
    }

    @Override
    public void delete(Long id) {
        jobApplicationRepository.deleteById(id);
    }
}