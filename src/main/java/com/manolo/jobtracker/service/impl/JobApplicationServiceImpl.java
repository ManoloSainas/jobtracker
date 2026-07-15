package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.enums.Role;
import com.manolo.jobtracker.mapper.JobApplicationMapper;
import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.exception.ApplicationNotFoundException;
import com.manolo.jobtracker.exception.BadRequestException;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.enums.ErrorCode;
import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.JobApplicationRepository;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.security.SecurityUtils;
import com.manolo.jobtracker.service.JobApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final TagRepository tagRepository;
    private final SecurityUtils securityUtils;


    public JobApplicationServiceImpl(
            JobApplicationRepository jobApplicationRepository,
            TagRepository tagRepository,
            SecurityUtils securityUtils
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.tagRepository = tagRepository;
        this.securityUtils = securityUtils;
    }


    @Override
    public JobApplicationResponseDto createApplication(JobApplicationRequestDto dto) {

        User user = securityUtils.getCurrentUser().getUser();

        log.debug(
                "Creazione candidatura avviata: userId={}, company={}, position={}",
                user.getId(),
                dto.getCompany(),
                dto.getPosition()
        );


        if (jobApplicationRepository.existsByUserIdAndCompanyAndPosition(
                user.getId(),
                dto.getCompany(),
                dto.getPosition()
        )) {

            throw new ConflictException(
                    "Hai già inviato una candidatura per questa posizione",
                    ErrorCode.APPLICATION_ALREADY_EXISTS
            );
        }


        Set<Tag> tags = validateAndGetTags(dto.getTagsIds());


        JobApplication application = new JobApplication();

        application.setStatus(dto.getStatus());
        application.setCompany(dto.getCompany());
        application.setPosition(dto.getPosition());
        application.setApplicationDate(LocalDate.now());
        application.setUser(user);
        application.setTags(tags);


        application = jobApplicationRepository.save(application);


        log.info(
                "Candidatura creata con successo: id={}, userId={}, company={}, position={}",
                application.getId(),
                user.getId(),
                application.getCompany(),
                application.getPosition()
        );


        return JobApplicationMapper.toResponse(application);
    }


    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponseDto getById(Long id) {

        log.debug("Richiesta getById JobApplication id={}", id);


        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));


        checkOwnership(application);


        return JobApplicationMapper.toResponse(application);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponseDto> getAll(Pageable pageable) {

        User currentUser = securityUtils.getCurrentUser().getUser();


        log.debug(
                "Richiesta lista candidature: userId={}, role={}",
                currentUser.getId(),
                currentUser.getRole()
        );


        if (currentUser.getRole() == Role.ADMIN) {

            return jobApplicationRepository.findAll(pageable)
                    .map(JobApplicationMapper::toResponse);

        }


        return jobApplicationRepository.findByUserId(
                currentUser.getId(),
                pageable
        ).map(JobApplicationMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponseDto> getMyApplications(Pageable pageable) {

        User currentUser = securityUtils.getCurrentUser().getUser();

        log.debug(
                "Richiesta candidature utente autenticato: userId={}",
                currentUser.getId()
        );

        return jobApplicationRepository
                .findByUserId(currentUser.getId(), pageable)
                .map(JobApplicationMapper::toResponse);
    }


    @Override
    public JobApplicationResponseDto patch(Long id, JobApplicationPatchDto dto) {

        log.debug(
                "Patch JobApplication avviato: id={}, status={}, tagsIds={}",
                id,
                dto.getStatus(),
                dto.getTagsIds()
        );


        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));


        checkOwnership(application);


        if (dto.getStatus() != null) {
            application.setStatus(dto.getStatus());
        }


        if (dto.getTagsIds() != null) {
            application.setTags(validateAndGetTags(dto.getTagsIds()));
        }


        application = jobApplicationRepository.save(application);


        log.info(
                "JobApplication aggiornata con successo: id={}",
                id
        );


        return JobApplicationMapper.toResponse(application);
    }


    @Override
    public void delete(Long id) {

        log.debug(
                "Richiesta eliminazione JobApplication id={}",
                id
        );


        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "JobApplication non trovata con id: " + id
                ));


        checkOwnership(application);


        jobApplicationRepository.delete(application);


        log.info(
                "JobApplication eliminata con successo: id={}",
                id
        );
    }


    private Set<Tag> validateAndGetTags(List<Long> tagsIds) {

        if (tagsIds == null || tagsIds.isEmpty()) {

            throw new BadRequestException(
                    "Devi inserire almeno un tag",
                    ErrorCode.INVALID_TAG_LIST
            );
        }


        Set<Tag> tags = new HashSet<>(
                tagRepository.findAllById(tagsIds)
        );


        if (tags.size() != tagsIds.size()) {

            throw new BadRequestException(
                    "Uno o più tag non validi",
                    ErrorCode.INVALID_TAG_LIST
            );
        }


        return tags;
    }


    private void checkOwnership(JobApplication application) {

        User currentUser = securityUtils.getCurrentUser().getUser();


        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }


        if (!application.getUser().getId()
                .equals(currentUser.getId())) {

            throw new ApplicationNotFoundException(
                    "JobApplication non trovata"
            );
        }
    }
}