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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        log.debug("Creazione Tag avviata: name={}", dto.getName());

        if (tagRepository.findByName(dto.getName()).isPresent()) {

            throw new ConflictException(
                    "Tag già esistente: " + dto.getName(),
                    ErrorCode.TAG_ALREADY_EXISTS
            );
        }

        Tag tag = TagMapper.toEntity(dto);
        tag = tagRepository.save(tag);

        log.info("Tag creato con successo: id={}, name={}", tag.getId(), tag.getName());

        return TagMapper.toResponse(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDto> getAllTags() {

        log.debug("Richiesta lista completa Tag");

        return tagRepository.findAll()
                .stream()
                .map(TagMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponseDto getById(Long id) {

        log.debug("Richiesta Tag per id={}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(
                        "Tag non trovato con id: " + id
                ));

        return TagMapper.toResponse(tag);
    }
}