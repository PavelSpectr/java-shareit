package ru.practicum.shareit.itemRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortInfoDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDetailsInfoDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortInfoDto> items;
}