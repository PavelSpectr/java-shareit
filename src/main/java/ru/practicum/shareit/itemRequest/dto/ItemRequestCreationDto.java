package ru.practicum.shareit.itemRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestCreationDto {
    @NotBlank(
            groups = {ValidationGroup.OnCreate.class},
            message = "Описание не должно быть пустым или содержать только пробельные символы"
    )
    private String description;
}