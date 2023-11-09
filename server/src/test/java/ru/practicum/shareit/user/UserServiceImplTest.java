package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для UserServiceImpl")
class UserServiceImplTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Возвращать пользователя по id")
    public void shouldReturnUserById() {
        User user = generator.nextObject(User.class);
        Long userId = user.getId();

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        User resultUser = userService.getUserById(userId);

        Assertions.assertEquals(user, resultUser);
    }

    @Test
    @DisplayName("Бросить исключение при получении пользователя с неверным id")
    public void shouldThrowExceptionWhenGetUserWithInvalidId() {
        Long invalidUserId = 1L;

        Mockito.when(userRepository.findById(Mockito.eq(invalidUserId))).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.getUserById(invalidUserId)
        );

        Assertions.assertEquals("Пользователь #" + invalidUserId + " не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Возвращать список пользователей")
    public void shouldReturnUsers() {
        List<User> users = generator.objects(User.class, 3).collect(Collectors.toList());

        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<User> resultUsers = userService.getUsers();

        Assertions.assertEquals(users, resultUsers);
    }

    @Test
    @DisplayName("Создает и вернуть пользователя")
    public void shouldCreateAndReturnUser() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        Assertions.assertEquals(user, createdUser);
    }

    @Test
    @DisplayName("Обновить и вернуть пользователя")
    public void shouldUpdateAndReturnUser() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user);
        Assertions.assertEquals(user, updatedUser);
    }

    @Test
    @DisplayName("Сохраняет текущие email и имя пользователя" +
            "при обновлении пользователя с пустыми значениями name и email")
    public void shouldSaveCurrentNameAndEmailWhenUpdatingUserWithEmptyValues() {
        User currentUser = generator.nextObject(User.class);
        Long userId = currentUser.getId();

        User userWithEmptyFields = new User();
        userWithEmptyFields.setId(userId);
        userWithEmptyFields.setName(null);
        userWithEmptyFields.setEmail(null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUser(userWithEmptyFields);

        Assertions.assertEquals(currentUser.getName(), updatedUser.getName());
        Assertions.assertEquals(currentUser.getEmail(), updatedUser.getEmail());

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).save(updatedUser);
    }

    @Test
    @DisplayName("Бросить исключение при обновлении пользователя с неверным id")
    public void shouldThrowExceptionWhenUpdatingUserWithInvalidId() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(user)
        );

        Assertions.assertEquals("Пользователь #" + user.getId() + " не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Удалить пользователя по id")
    public void shouldDeleteUser() {
        final Long userId = generator.nextObject(Long.class);

        Mockito.doNothing().when(userRepository).deleteById(Mockito.any());

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.eq(userId));
    }
}