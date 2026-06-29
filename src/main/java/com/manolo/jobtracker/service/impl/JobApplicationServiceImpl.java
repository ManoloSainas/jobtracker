package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.dto.mapper.JobApplicationMapper;
import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.JobApplicationRepository;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.repository.UserRepository;
import com.manolo.jobtracker.service.JobApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public JobApplicationServiceImpl(
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            TagRepository tagRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public JobApplicationResponseDto createApplication(JobApplicationRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User non trovato con id: " + dto.getUserId()));

        Set<Tag> tags = new HashSet<>(
                tagRepository.findAllById(dto.getTagsIds())
        );

        JobApplication application = new JobApplication();
        application.setStatus(dto.getStatus());
        application.setCompany(dto.getCompany());
        application.setPosition(dto.getPosition());
        application.setApplicationDate(dto.getApplicationDate());
        application.setUser(user);
        application.setTags(tags);

        application = jobApplicationRepository.save(application);

        return JobApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponseDto getById(Long id) {

        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplication non trovata con id: " + id));

        return JobApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponseDto> getAll() {

        return jobApplicationRepository.findAll()
                .stream()
                .map(JobApplicationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponseDto> getByUserId(Long userId) {

        return jobApplicationRepository.findByUserId(userId)
                .stream()
                .map(JobApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public JobApplicationResponseDto update(Long id, JobApplicationRequestDto dto) {

        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplication non trovata con id: " + id));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User non trovato con id: " + dto.getUserId()));

        Set<Tag> tags = new HashSet<>(
                tagRepository.findAllById(dto.getTagsIds())
        );

        application.setStatus(dto.getStatus());
        application.setCompany(dto.getCompany());
        application.setPosition(dto.getPosition());
        application.setApplicationDate(dto.getApplicationDate());
        application.setUser(user);
        application.setTags(tags);

        application = jobApplicationRepository.save(application);

        return JobApplicationMapper.toResponse(application);
    }

    @Override
    public void delete(Long id) {

        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobApplication non trovata con id: " + id));

        jobApplicationRepository.delete(application);
    }
}