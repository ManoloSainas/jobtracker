package com.manolo.jobtracker.service.impl;

import com.manolo.jobtracker.dto.request.TagRequestDto;
import com.manolo.jobtracker.dto.response.TagResponseDto;
import com.manolo.jobtracker.mapper.TagMapper;
import com.manolo.jobtracker.exception.ConflictException;
import com.manolo.jobtracker.exception.TagNotFoundException;
import com.manolo.jobtracker.model.Tag;
import com.manolo.jobtracker.enums.ErrorCode;
import com.manolo.jobtracker.repository.TagRepository;
import com.manolo.jobtracker.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagResponseDto createTag(TagRequestDto dto) {

        String normalizedName = dto.getName()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);


        log.debug("Creazione Tag avviata: name={}", normalizedName);


        if (tagRepository.findByName(normalizedName).isPresent()) {

            throw new ConflictException(
                    "Tag già esistente: " + normalizedName,
                    ErrorCode.TAG_ALREADY_EXISTS
            );
        }


        Tag tag = TagMapper.toEntity(dto);
        tag.setName(normalizedName);

        tag = tagRepository.save(tag);


        log.info(
                "Tag creato con successo: id={}, name={}",
                tag.getId(),
                tag.getName()
        );


        return TagMapper.toResponse(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagResponseDto> getAllTags(Pageable pageable) {

        log.debug(
                "Richiesta lista paginata Tag: page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return tagRepository.findAll(pageable)
                .map(TagMapper::toResponse);
    }
}