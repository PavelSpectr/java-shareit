package ru.practicum.shareit.itemRequest.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreationDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDetailsInfoDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDetailsInfoDto toItemRequestDetailsInfoDto(ItemRequest itemRequest) {
        return ItemRequestDetailsInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }

    public List<ItemRequestDetailsInfoDto> toItemRequestDetailsInfoDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDetailsInfoDto)
                .collect(toList());
    }

    public ItemRequest toItemRequest(ItemRequestCreationDto itemRequestCreationDto) {
        return ItemRequest.builder()
                .description(itemRequestCreationDto.getDescription())
                .build();
    }
}
