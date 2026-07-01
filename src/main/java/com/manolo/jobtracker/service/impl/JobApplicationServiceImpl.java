package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.mapper.JobApplicationMapper;
import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.exception.ApplicationNotFoundException;
import com.manolo.jobtracker.exception.BadRequestException;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.UserNotFoundException;
import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.model.enums.ErrorCode;
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

        if (jobApplicationRepository.existsByUserIdAndCompanyAndPosition(
                dto.getUserId(),
                dto.getCompany(),
                dto.getPosition()
        )) {
            throw new ConflictException(
                    "Hai già inviato una candidatura per questa posizione",
                    ErrorCode.APPLICATION_ALREADY_EXISTS
            );
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User non trovato con id: " + dto.getUserId()
                ));

        Set<Tag> tags = validateAndGetTags(dto.getTagsIds());

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
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));

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

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User non trovato con id: " + userId
                ));

        return jobApplicationRepository.findByUserId(userId)
                .stream()
                .map(JobApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public JobApplicationResponseDto patch(Long id, JobApplicationPatchDto dto) {

        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));

        if (dto.getStatus() != null) {
            application.setStatus(dto.getStatus());
        }

        if (dto.getTagsIds() != null) {
            application.setTags(validateAndGetTags(dto.getTagsIds()));
        }

        return JobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Override
    public void delete(Long id) {

        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));


        jobApplicationRepository.delete(application);
    }

    private Set<Tag> validateAndGetTags(List<Long> tagsIds) {

        if (tagsIds == null || tagsIds.isEmpty()) {
            throw new BadRequestException(
                    "Devi inserire almeno un tag",
                    ErrorCode.INVALID_TAG_LIST
            );
        }

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagsIds));

        if (tags.size() != tagsIds.size()) {
            throw new BadRequestException(
                    "Uno o più tag non validi",
                    ErrorCode.INVALID_TAG_LIST
            );
        }

        return tags;
    }
}