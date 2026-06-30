package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.dto.mapper.TagMapper;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.TagNotFoundException;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.model.enums.ErrorCode;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.service.TagService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagResponseDto createTag(TagRequestDto dto) {

        if (tagRepository.findByName(dto.getName()).isPresent()) {
            throw new ConflictException("Tag già esistente: " + dto.getName(),
                    ErrorCode.TAG_ALREADY_EXISTS);
        }

        Tag tag = TagMapper.toEntity(dto);

        tag = tagRepository.save(tag);

        return TagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    public List<TagResponseDto> getAllTags() {

        return tagRepository.findAll()
                .stream()
                .map(TagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TagResponseDto getById(Long id) {

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Tag non trovato con id: " + id));

        return TagMapper.toResponse(tag);
    }
}