package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.users.UserInvalidDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.inMemory.InMemoryUserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(inMemoryUserStorage.add(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.update(UserMapper.toUser(userDto), userId));
    }

    public Collection<UserDto> getAllUsers() {
        return inMemoryUserStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.getById(userId)
                .orElseThrow(() -> new UserInvalidDataException("Попытка получить пользователя с отсутствующим id: " + userId)));
    }

    public void deleteUserById(Long userId) {
        inMemoryUserStorage.deleteById(userId);
    }
}