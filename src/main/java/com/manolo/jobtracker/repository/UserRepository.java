package com.manolo.jobtracker.repository;

import com.manolo.jobtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
