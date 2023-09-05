package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.item.ItemBadRequestException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.users.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto getItemInfo(Long itemId) {
        return itemStorage.getItemInfo(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
    }

    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        User user = validateAndGetUser(userId);
        validateItemDto(itemDto);

        Item item = ItemMapper.toItem(itemDto, user, null);
        Item addedItem = itemStorage.add(item);

        return ItemMapper.toItemDto(addedItem);
    }

    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        User user = validateAndGetUser(userId);
        if (itemDto.getId() != null && !Objects.equals(itemDto.getId(), itemId)) {
            throw new ItemBadRequestException("Попытка обновления вещи по id, где id не совпадает с id в itemDto");
        }
        Item item = ItemMapper.toItem(itemDto, user, null);
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, userId, item));
    }

    public Collection<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getById(userId).stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> getAvailableItemsForRentByKeyword(String text) {
        return itemStorage.getByKeyWords(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null ||
                itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            throw new ItemBadRequestException("Попытка добавить элемент с отсутствующими полями");
        }
    }

    private User validateAndGetUser(Long userId) {
        return userStorage.getById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }
}