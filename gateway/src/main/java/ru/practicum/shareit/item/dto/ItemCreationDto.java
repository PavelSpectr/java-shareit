package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemCreationDto {
    private Long id;

    @NotBlank(
            groups = {ValidationGroup.OnCreate.class},
            message = "Имя не должно быть пустым или содержать только пробельные символы"
    )
    @Size(
            groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            max = 255
    )
    private String name;

    @NotBlank(
            groups = {ValidationGroup.OnCreate.class},
            message = "Описание не должно быть пустым или содержать только пробельные символы"
    )
    private String description;

    @NotNull(
            groups = {ValidationGroup.OnCreate.class},
            message = "Признак доступности вещи не должен быть null"
    )
    private Boolean available;

    private Long requestId;
}