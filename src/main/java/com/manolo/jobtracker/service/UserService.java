package com.manolo.jobtracker.service;

import com.manolo.jobtracker.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);
}
