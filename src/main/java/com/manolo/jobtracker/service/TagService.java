package com.manolo.jobtracker.service;

import com.manolo.jobtracker.model.Tag;

import java.util.List;

public interface TagService {

    Tag createTag(Tag tag);

    List<Tag> getAllTags();

    Tag getById(Long id);
}
