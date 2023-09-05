package ru.practicum.shareit.item.storage.inMemory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.item.ItemBadRequestException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public Optional<Item> getItemInfo(Long itemId) {
        return Optional.ofNullable(itemMap.get(itemId));
    }

    @Override
    public Item add(Item item) {
        Long itemId = getNextId();
        item.setId(itemId);
        itemMap.put(itemId, item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item itemUpdate) {
        Item item = itemMap.get(itemId);

        if (item == null) {
            throw new ItemBadRequestException("Попытка обновить элемент с неправильным id");
        }

        validateOwner(userId, item);
        updateItemProperties(item, itemUpdate);
        return item;
    }

    private void validateOwner(Long userId, Item item) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ItemNotFoundException("Попытка обновить элемент другого пользователя");
        }
    }

    private void updateItemProperties(Item item, Item itemUpdate) {
        if (itemUpdate.getName() != null) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }
    }

    @Override
    public Collection<Item> getById(Long userId) {
        return itemMap.values();
    }

    @Override
    public Collection<Item> getByKeyWords(String text) {
        if (text == null || text.isEmpty()) return List.of();
        String lowerCaseText = text.toLowerCase();

        return itemMap.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }
}