package ru.practicum.shareit.user.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.users.UserBadRequestException;
import ru.practicum.shareit.exception.users.UserInvalidDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersMap = new HashMap<>();
    private final Set<String> emailSet = new HashSet<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public User add(User user) {
        validateUserEmail(user.getEmail());
        Long userId = getNextId();
        log.info("Добавлен пользователь. userId = {}, user = {}", userId, user);
        user.setId(userId);
        usersMap.put(userId, user);
        emailSet.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        validateUserExists(userId);
        validateUserEmail(user.getEmail());
        User userUpdate = usersMap.get(userId);
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userUpdate.setEmail(user.getEmail());
        }
        log.info("User id = {} обновлён", userId);
        return userUpdate;
    }

    @Override
    public Collection<User> getAll() {
        return usersMap.values();
    }

    @Override
    public void deleteById(Long userId) {
        validateUserExists(userId);
        usersMap.remove(userId);
        log.info("user id = {} удалён", userId);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(usersMap.get(id));
    }

    private void validateUserEmail(String email) { //Исправил алгоритм
        if (emailSet.contains(email)) {
            throw new UserInvalidDataException("Ошибка обновления. Такая почта уже существует");
        }
    }

    private void validateUserExists(Long id) {
        if (!Objects.equals(id, usersMap.get(id).getId())) { //Поменял алгоритм
            throw new UserBadRequestException("Ошибка обновления пользователя. Id пользователя не найден");
        }
    }
}