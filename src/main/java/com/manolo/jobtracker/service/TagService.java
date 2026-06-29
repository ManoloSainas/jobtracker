package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;

import java.util.List;

public interface TagService {

    TagResponseDto createTag(TagRequestDto dto);

    List<TagResponseDto> getAllTags();

    TagResponseDto getById(Long id);
}