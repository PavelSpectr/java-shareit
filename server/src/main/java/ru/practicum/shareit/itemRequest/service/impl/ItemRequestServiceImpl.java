package ru.practicum.shareit.itemRequest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest getItemRequestById(Long itemRequestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь #" + userId + " не найден.");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Запрос #" + itemRequestId + " не найден."));

        setItems(itemRequest);

        return itemRequest;
    }

    @Override
    public List<ItemRequest> getItemRequests(Long userId, Pageable pageable) {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(userId, pageable);

        setItems(itemRequests);

        return itemRequests;
    }

    @Override
    public List<ItemRequest> getItemRequestsByRequestorId(Long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Пользователь #" + requestorId + " не найден.");
        }

        List<ItemRequest> itemRequests = itemRequestRepository
                .findItemRequestsByRequestorIdOrderByCreatedDesc(requestorId);

        setItems(itemRequests);

        return itemRequests;
    }

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest, Long requestorId) {
        LocalDateTime currentDate = LocalDateTime.now();

        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь #" + requestorId + " не найден."));

        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(currentDate);
        return itemRequestRepository.save(itemRequest);
    }

    private void setItems(List<ItemRequest> itemRequests) {
        Map<ItemRequest, List<Item>> itemsByRequest = itemRepository.findByRequestIn(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequest, Collectors.toList()));

        itemRequests.forEach(itemRequest -> {
            List<ItemShortInfoDto> items = itemsByRequest.getOrDefault(itemRequest, Collections.emptyList()).stream()
                    .map(ItemMapper::toItemShortInfoDto)
                    .collect(Collectors.toList());

            itemRequest.setItems(items);
        });
    }

    private void setItems(ItemRequest itemRequest) {
        List<ItemShortInfoDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemShortInfoDto)
                .collect(Collectors.toList());

        itemRequest.setItems(items);
    }
}
