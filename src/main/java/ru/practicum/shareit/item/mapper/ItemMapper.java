package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDetailsInfoDto;
import ru.practicum.shareit.item.dto.ItemShortInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ItemMapper {
    public ItemDetailsInfoDto toItemDetailsInfoDto(Item item) {
        return ItemDetailsInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .comments(item.getComments())
                .build();
    }

    public List<ItemDetailsInfoDto> toItemDetailsInfoDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDetailsInfoDto)
                .collect(toList());
    }

    public ItemShortInfoDto toItemShortInfoDto(Item item) {
        return ItemShortInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item toItem(ItemCreationDto itemDto) {
        Long requestId = itemDto.getRequestId();

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(Objects.nonNull(requestId) ? ItemRequest.builder().id(requestId).build() : null)
                .build();
    }
}