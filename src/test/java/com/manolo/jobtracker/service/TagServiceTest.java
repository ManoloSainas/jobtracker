package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TagServiceTest {


    private TagServiceImpl tagService;


    @Mock
    private TagRepository tagRepository;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

        tagService = new TagServiceImpl(
                tagRepository
        );
    }



    @Test
    void createTag_shouldCreateTag(){


        TagRequestDto dto =
                new TagRequestDto(
                        "Java"
                );


        when(tagRepository.findByName("java"))
                .thenReturn(Optional.empty());


        Tag savedTag = new Tag();

        savedTag.setId(1L);
        savedTag.setName("java");


        when(tagRepository.save(any(Tag.class)))
                .thenReturn(savedTag);



        TagResponseDto response =
                tagService.createTag(dto);



        assertNotNull(response);
        assertEquals(
                "java",
                response.getName()
        );


        verify(tagRepository)
                .save(any(Tag.class));

    }




    @Test
    void createTag_shouldFailIfAlreadyExists(){


        Tag tag = new Tag();

        tag.setName("java");


        when(tagRepository.findByName("java"))
                .thenReturn(Optional.of(tag));



        TagRequestDto dto =
                new TagRequestDto(
                        "Java"
                );



        assertThrows(
                ConflictException.class,
                () -> tagService.createTag(dto)
        );


        verify(tagRepository, never())
                .save(any());

    }

}