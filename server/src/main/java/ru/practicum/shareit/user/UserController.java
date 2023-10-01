package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        log.debug("+ getUsers");
        List<UserDto> users = UserMapper.toUserDto(userService.getUsers());
        log.debug("- getUsers: {}", users);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.debug("+ getUserById: userId={}", userId);
        UserDto user = UserMapper.toUserDto(userService.getUserById(userId));
        log.debug("- getUserById: {}", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        log.debug("+ createUser: {}", user);
        UserDto createdUser = UserMapper.toUserDto(userService.createUser(UserMapper.toUser(user)));
        log.debug("- createUser: {}", createdUser);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        log.debug("+ updateUser: {}", user);
        user.setId(userId);
        UserDto updatedUser = UserMapper.toUserDto(userService.updateUser(UserMapper.toUser(user)));
        log.debug("- updateUser: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("+ deleteUser: userId={}", userId);
        userService.deleteUser(userId);
        log.debug("- deleteUser");
    }
}