package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Возвращать список пользователей")
    public void shouldReturnUsers() throws Exception {
        List<User> users = generator.objects(User.class, 2).collect(Collectors.toList());

        Mockito.when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(users.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(users.get(1).getEmail()));

        Mockito.verify(userService).getUsers();
    }

    @Test
    @DisplayName("Возвращать пользователя по id")
    public void shouldReturnUserById() throws Exception {
        User user = generator.nextObject(User.class);
        Long userId = user.getId();

        Mockito.when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        Mockito.verify(userService).getUserById(userId);
    }

    @Test
    @DisplayName("Создать и вернуть пользователя")
    public void shouldCreateAndReturnUser() throws Exception {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setEmail("mail@mail.ru");

        Mockito.when(userService.createUser(Mockito.any(User.class)))
                .thenReturn(UserMapper.toUser(userDto));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).createUser(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Обновить и вернуть пользователя")
    public void shouldUpdateAndReturnUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setEmail("mail@mail.ru");

        Mockito.when(userService.updateUser(Mockito.any(User.class)))
                .thenReturn(UserMapper.toUser(userDto));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).updateUser(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Удалить пользователя")
    public void shouldDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(userId);
    }
}