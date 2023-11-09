package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroup;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.debug("+ getUsers");
        ResponseEntity<Object> users = userClient.getUsers();
        log.debug("- getUsers: {}", users);
        return users;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.debug("+ getUserById: userId={}", userId);
        ResponseEntity<Object> user = userClient.getUserById(userId);
        log.debug("- getUserById: {}", user);
        return user;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(ValidationGroup.OnCreate.class) UserDto user) {
        log.debug("+ createUser: {}", user);
        ResponseEntity<Object> createdUser = userClient.createUser(user);
        log.debug("- createUser: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                              @RequestBody @Validated(ValidationGroup.OnUpdate.class) UserDto user) {
        log.debug("+ updateUser: {}", user);
        ResponseEntity<Object> updatedUser = userClient.updateUser(userId, user);
        log.debug("- updateUser: {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.debug("+ deleteUser: userId={}", userId);
        ResponseEntity<Object> result = userClient.deleteUser(userId);
        log.debug("- deleteUser");
        return result;
    }
}