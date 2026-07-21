package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    @Test
    @DisplayName("createTag: success normalizes name and saves")
    void createTag_success() {
        TagRequestDto dto = new TagRequestDto();
        dto.setName("  Java ");

        when(tagRepository.findByName("java")).thenReturn(Optional.empty());

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        when(tagRepository.save(captor.capture())).thenAnswer(i -> {
            Tag t = i.getArgument(0);
            t.setId(7L);
            return t;
        });

        TagResponseDto out = tagService.createTag(dto);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(7L);
        assertThat(captor.getValue().getName()).isEqualTo("java");
    }

    @Test
    @DisplayName("createTag: conflict when exists")
    void createTag_conflict() {
        TagRequestDto dto = new TagRequestDto(); dto.setName("Spring");
        when(tagRepository.findByName("spring")).thenReturn(Optional.of(new Tag()));
        assertThrows(ConflictException.class, () -> tagService.createTag(dto));
        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllTags: returns paged list")
    void getAllTags_paged() {
        Tag t = new Tag(); t.setId(1L); t.setName("go");
        when(tagRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(t)));
        var page = tagService.getAllTags(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("go");
    }
}
