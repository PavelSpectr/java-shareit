package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreationDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDetailsInfoDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDetailsInfoDto> getItemRequestsByRequestorId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long requestorId
    ) {
        log.debug("+ getItemRequestsByRequestorId: requestorId={}", requestorId);
        List<ItemRequestDetailsInfoDto> itemRequest = ItemRequestMapper
                .toItemRequestDetailsInfoDto(itemRequestService.getItemRequestsByRequestorId(requestorId));
        log.debug("- getItemRequestsByRequestorId: {}", itemRequest);
        return itemRequest;
    }

    @GetMapping("/all")
    public List<ItemRequestDetailsInfoDto> getItemRequests(
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer offset,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer limit,
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId
    ) {
        log.debug("+ getItemRequestsByRequestorId: from={}, size={}, userId={}", offset, limit, userId);

        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<ItemRequestDetailsInfoDto> itemRequests = ItemRequestMapper
                .toItemRequestDetailsInfoDto(itemRequestService.getItemRequests(userId, pageable));

        log.debug("- getItemRequestsByRequestorId: {}", itemRequests);
        return itemRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDetailsInfoDto getItemRequestById(@PathVariable Long itemRequestId,
                                                        @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ getItemRequestById: itemRequestId={}", itemRequestId);
        ItemRequestDetailsInfoDto itemRequest = ItemRequestMapper
                .toItemRequestDetailsInfoDto(itemRequestService.getItemRequestById(itemRequestId, userId));
        log.debug("- getItemRequestById: {}", itemRequest);
        return itemRequest;
    }

    @PostMapping
    public ItemRequestDetailsInfoDto createItemRequest(
            @RequestBody @Validated(ValidationGroup.OnCreate.class) ItemRequestCreationDto itemRequest,
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId
    ) {
        log.debug("+ createItemRequest: itemRequest={}, userId={}", itemRequest, userId);

        ItemRequest createdItemRequest = itemRequestService
                .createItemRequest(ItemRequestMapper.toItemRequest(itemRequest), userId);

        ItemRequestDetailsInfoDto createdItemRequestDto = ItemRequestMapper
                .toItemRequestDetailsInfoDto(createdItemRequest);

        log.debug("- createItemRequest: {}", createdItemRequestDto);

        return createdItemRequestDto;
    }
}
