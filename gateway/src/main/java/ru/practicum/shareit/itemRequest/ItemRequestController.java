package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreationDto;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByRequestorId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long requestorId
    ) {
        log.debug("+ getItemRequestsByRequestorId: requestorId={}", requestorId);
        ResponseEntity<Object> itemRequest = itemRequestClient.getItemRequestsByRequestorId(requestorId);
        log.debug("- getItemRequestsByRequestorId: {}", itemRequest);
        return itemRequest;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size,
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId
    ) {
        log.debug("+ getItemRequestsByRequestorId: from={}, size={}, userId={}", from, size, userId);
        ResponseEntity<Object> itemRequests = itemRequestClient.getItemRequests(from, size, userId);
        log.debug("- getItemRequestsByRequestorId: {}", itemRequests);
        return itemRequests;
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long itemRequestId,
                                                     @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ getItemRequestById: itemRequestId={}", itemRequestId);
        ResponseEntity<Object> itemRequest = itemRequestClient.getItemRequestById(itemRequestId, userId);
        log.debug("- getItemRequestById: {}", itemRequest);
        return itemRequest;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestBody @Validated(ValidationGroup.OnCreate.class) ItemRequestCreationDto itemRequest,
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId
    ) {
        log.debug("+ createItemRequest: itemRequest={}, userId={}", itemRequest, userId);
        ResponseEntity<Object> createdItemRequest = itemRequestClient.createItemRequest(itemRequest, userId);
        log.debug("- createItemRequest: {}", createdItemRequest);
        return createdItemRequest;
    }
}