package com.manolo.jobtracker.service;


import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.enums.ErrorCode;
import com.manolo.jobtracker.enums.Role;
import com.manolo.jobtracker.enums.Status;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.BadRequestException;
import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.JobApplicationRepository;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.security.CustomUserDetails;
import com.manolo.jobtracker.security.SecurityUtils;
import com.manolo.jobtracker.service.impl.JobApplicationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class JobApplicationServiceTest {


    @Mock
    private JobApplicationRepository jobApplicationRepository;


    @Mock
    private TagRepository tagRepository;


    @Mock
    private SecurityUtils securityUtils;


    @InjectMocks
    private JobApplicationServiceImpl service;


    private User user;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();

        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(Role.USER);


        CustomUserDetails userDetails =
                new CustomUserDetails(user);


        when(securityUtils.getCurrentUser())
                .thenReturn(userDetails);
    }



    @Test
    void createApplication_shouldCreateSuccessfully() {


        JobApplicationRequestDto dto =
                new JobApplicationRequestDto();


        dto.setStatus(Status.APPLIED);
        dto.setCompany("Google");
        dto.setPosition("Backend Developer");
        dto.setTagsIds(List.of(1L));



        Tag tag = new Tag();

        tag.setId(1L);
        tag.setName("Java");


        when(jobApplicationRepository
                .existsByUserIdAndCompanyAndPosition(
                        1L,
                        "Google",
                        "Backend Developer"
                ))
                .thenReturn(false);


        when(tagRepository.findAllById(List.of(1L)))
                .thenReturn(List.of(tag));


        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> {

                    JobApplication app =
                            invocation.getArgument(0);

                    app.setId(10L);

                    return app;
                });



        JobApplicationResponseDto response =
                service.createApplication(dto);



        assertNotNull(response);

        assertEquals(
                Status.APPLIED,
                response.getStatus()
        );

        assertEquals(
                "Google",
                response.getCompany()
        );


        verify(jobApplicationRepository)
                .save(any(JobApplication.class));
    }




    @Test
    void createApplication_shouldThrowException_whenDuplicate() {


        JobApplicationRequestDto dto =
                new JobApplicationRequestDto();


        dto.setStatus(Status.APPLIED);
        dto.setCompany("Google");
        dto.setPosition("Backend Developer");
        dto.setTagsIds(List.of(1L));



        when(jobApplicationRepository
                .existsByUserIdAndCompanyAndPosition(
                        1L,
                        "Google",
                        "Backend Developer"
                ))
                .thenReturn(true);



        ConflictException exception =
                assertThrows(
                        ConflictException.class,
                        () -> service.createApplication(dto)
                );



        assertEquals(
                ErrorCode.APPLICATION_ALREADY_EXISTS,
                exception.getErrorCode()
        );


        verify(jobApplicationRepository, never())
                .save(any());
    }




    @Test
    void createApplication_shouldThrowException_whenTagInvalid() {


        JobApplicationRequestDto dto =
                new JobApplicationRequestDto();


        dto.setStatus(Status.APPLIED);
        dto.setCompany("Google");
        dto.setPosition("Backend Developer");
        dto.setTagsIds(List.of(1L,2L));



        when(jobApplicationRepository
                .existsByUserIdAndCompanyAndPosition(
                        1L,
                        "Google",
                        "Backend Developer"
                ))
                .thenReturn(false);



        when(tagRepository.findAllById(List.of(1L,2L)))
                .thenReturn(
                        List.of(new Tag())
                );



        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> service.createApplication(dto)
                );



        assertEquals(
                ErrorCode.INVALID_TAG_LIST,
                exception.getErrorCode()
        );


        verify(jobApplicationRepository, never())
                .save(any());
    }

}