package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.JobApplicationPatchDto;
import com.manolo.jobtracker.dto.request.JobApplicationRequestDto;
import com.manolo.jobtracker.dto.response.JobApplicationResponseDto;
import com.manolo.jobtracker.enums.Role;
import com.manolo.jobtracker.exception.ApplicationNotFoundException;
import com.manolo.jobtracker.exception.BadRequestException;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.model.JobApplication;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.User;
import com.manolo.jobtracker.repository.JobApplicationRepository;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.security.CustomUserDetails;
import com.manolo.jobtracker.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

    @Mock
    JobApplicationRepository jobApplicationRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    SecurityUtils securityUtils;

    @InjectMocks
    JobApplicationServiceImpl jobApplicationService;

    private User currentUser;

    @BeforeEach
    void setup() {
        currentUser = new User();
        currentUser.setId(42L);
        currentUser.setEmail("user@example.com");
        currentUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("createApplication: success saves and returns dto")
    void createApplication_success() {
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        JobApplicationRequestDto dto = new JobApplicationRequestDto();
        dto.setCompany("Acme");
        dto.setPosition("Engineer");
        dto.setStatus(null);
        dto.setTagsIds(List.of(1L, 2L));

        when(jobApplicationRepository.existsByUserIdAndCompanyAndPosition(currentUser.getId(), "Acme", "Engineer")).thenReturn(false);

        Tag t1 = new Tag(); t1.setId(1L); t1.setName("java");
        Tag t2 = new Tag(); t2.setId(2L); t2.setName("spring");
        when(tagRepository.findAllById(dto.getTagsIds())).thenReturn(List.of(t1, t2));

        JobApplication saved = new JobApplication();
        saved.setId(100L);
        saved.setCompany("Acme");
        saved.setPosition("Engineer");
        saved.setApplicationDate(LocalDate.now());
        saved.setUser(currentUser);
        saved.setTags(new HashSet<>(List.of(t1, t2)));

        when(jobApplicationRepository.save(any())).thenReturn(saved);

        JobApplicationResponseDto out = jobApplicationService.createApplication(dto);

        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(100L);
        assertThat(out.getCompany()).isEqualTo("Acme");
        verify(jobApplicationRepository).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("createApplication: conflict when same application exists")
    void createApplication_conflict() {
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));
        JobApplicationRequestDto dto = new JobApplicationRequestDto();
        dto.setCompany("Acme"); dto.setPosition("Engineer"); dto.setTagsIds(List.of(1L));

        when(jobApplicationRepository.existsByUserIdAndCompanyAndPosition(currentUser.getId(), "Acme", "Engineer")).thenReturn(true);

        assertThrows(ConflictException.class, () -> jobApplicationService.createApplication(dto));
        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("createApplication: invalid tags throws BadRequestException")
    void createApplication_invalidTags() {
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));
        JobApplicationRequestDto dto = new JobApplicationRequestDto();
        dto.setCompany("Acme"); dto.setPosition("Engineer"); dto.setTagsIds(List.of(1L, 2L));

        when(jobApplicationRepository.existsByUserIdAndCompanyAndPosition(currentUser.getId(), "Acme", "Engineer")).thenReturn(false);
        when(tagRepository.findAllById(dto.getTagsIds())).thenReturn(List.of(new Tag())); // size mismatch

        assertThrows(BadRequestException.class, () -> jobApplicationService.createApplication(dto));
        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("getById: not found throws")
    void getById_notFound() {
        when(jobApplicationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () -> jobApplicationService.getById(999L));
    }

    @Test
    @DisplayName("getById: ownership enforced for non-admin")
    void getById_ownershipEnforced() {
        User owner = new User(); owner.setId(7L); owner.setRole(Role.USER);
        JobApplication app = new JobApplication(); app.setId(2L); app.setUser(owner);
        when(jobApplicationRepository.findById(2L)).thenReturn(Optional.of(app));
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        assertThrows(ApplicationNotFoundException.class, () -> jobApplicationService.getById(2L));
    }

    @Test
    @DisplayName("getById: admin can access any application")
    void getById_adminAccess() {
        User owner = new User(); owner.setId(7L); owner.setRole(Role.USER);
        JobApplication app = new JobApplication(); app.setId(2L); app.setUser(owner);
        when(jobApplicationRepository.findById(2L)).thenReturn(Optional.of(app));
        currentUser.setRole(Role.ADMIN);
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        JobApplicationResponseDto dto = jobApplicationService.getById(2L);
        assertThat(dto.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("getAll: admin returns all, user returns own")
    void getAll_roles() {
        JobApplication a = new JobApplication(); a.setId(11L); a.setUser(currentUser);
        when(jobApplicationRepository.findByUserId(eq(currentUser.getId()), any())).thenReturn(new PageImpl<>(List.of(a)));
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        var pageUser = jobApplicationService.getAll(PageRequest.of(0, 10));
        assertThat(pageUser.getTotalElements()).isEqualTo(1);

        currentUser.setRole(Role.ADMIN);
        when(jobApplicationRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new PageImpl<>(List.of(a)));
        var pageAdmin = jobApplicationService.getAll(PageRequest.of(0, 10));
        assertThat(pageAdmin.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("patch: updates status and tags when present")
    void patch_updates() {
        JobApplication existing = new JobApplication();
        existing.setId(3L);
        existing.setUser(currentUser);
        existing.setTags(new HashSet<>());

        when(jobApplicationRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        JobApplicationPatchDto dto = new JobApplicationPatchDto();
        dto.setStatus(null);
        dto.setTagsIds(List.of(5L));

        Tag tag = new Tag(); tag.setId(5L); tag.setName("kotlin");
        when(tagRepository.findAllById(dto.getTagsIds())).thenReturn(List.of(tag));
        when(jobApplicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        JobApplicationResponseDto out = jobApplicationService.patch(3L, dto);
        assertThat(out.getId()).isEqualTo(3L);
        verify(jobApplicationRepository).save(any());
    }

    @Test
    @DisplayName("delete: deletes when owner")
    void delete_ownerDeletes() {
        JobApplication existing = new JobApplication(); existing.setId(4L); existing.setUser(currentUser);
        when(jobApplicationRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        jobApplicationService.delete(4L);
        verify(jobApplicationRepository).delete(existing);
    }

    @Test
    @DisplayName("delete: non-owner cannot delete")
    void delete_nonOwnerThrows() {
        User other = new User(); other.setId(99L); other.setRole(Role.USER);
        JobApplication existing = new JobApplication(); existing.setId(5L); existing.setUser(other);
        when(jobApplicationRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(securityUtils.getCurrentUser()).thenReturn(new CustomUserDetails(currentUser));

        assertThrows(ApplicationNotFoundException.class, () -> jobApplicationService.delete(5L));
        verify(jobApplicationRepository, never()).delete(any());
    }
}
