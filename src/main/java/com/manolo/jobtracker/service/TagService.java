package com.manolo.jobtracker.service;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

    TagResponseDto createTag(TagRequestDto dto);

    Page<TagResponseDto> getAllTags(Pageable pageable);
}