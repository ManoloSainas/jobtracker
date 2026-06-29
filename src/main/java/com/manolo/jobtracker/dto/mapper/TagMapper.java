package com.manolo.jobtracker.dto.mapper;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.model.Tag;

public class TagMapper {

    public static Tag toEntity(TagRequestDto dto) {

        Tag tag = new Tag();

        tag.setName(dto.getName());

        return tag;
    }

    public static TagResponseDto toResponse(Tag tag) {

        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}