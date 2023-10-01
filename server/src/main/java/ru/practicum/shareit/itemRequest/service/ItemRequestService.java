package ru.practicum.shareit.itemRequest.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest getItemRequestById(Long itemRequestId, Long userId);

    List<ItemRequest> getItemRequests(Long userId, Pageable pageable);

    List<ItemRequest> getItemRequestsByRequestorId(Long requestorId);

    ItemRequest createItemRequest(ItemRequest itemRequest, Long requestorId);
}