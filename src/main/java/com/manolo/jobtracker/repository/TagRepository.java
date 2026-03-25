package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
