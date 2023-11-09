package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(
            groups = ValidationGroup.OnCreate.class,
            message = "Имя не должно быть пустым или содержать только пробельные символы"
    )
    @Size(
            groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            max = 255
    )
    private String name;

    @NotEmpty(
            groups = {ValidationGroup.OnCreate.class},
            message = "Электронный адрес не должен быть пустым"
    )
    @Email(
            groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            message = "Неверный адрес электронной почты"
    )
    @Size(
            groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            max = 320
    )
    private String email;
}