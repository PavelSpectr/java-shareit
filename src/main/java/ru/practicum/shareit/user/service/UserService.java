package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    List<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);
}
