package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long ownerId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size
    ) {
        log.debug("+ getItemsByOwnerId: ownerId={}", ownerId);
        ResponseEntity<Object> items = itemClient.getItemsByOwnerId(ownerId, from, size);
        log.debug("- getItemsByOwnerId: {}", items);
        return items;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ getItemById: itemId={}", itemId);
        ResponseEntity<Object> item = itemClient.getItemById(itemId, userId);
        log.debug("- getItemById: {}", item);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size
    ) {
        log.debug("+ searchItems: text={}", text);
        ResponseEntity<Object> items = itemClient.searchItems(text, from, size);
        log.debug("- searchItems: {}", items);
        return items;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(ValidationGroup.OnCreate.class) ItemCreationDto item,
                                             @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("+ createItem: item={}, userId={}", item, userId);
        ResponseEntity<Object> createdItem = itemClient.createItem(item, userId);
        log.debug("- createItem: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestBody @Validated(ValidationGroup.OnUpdate.class) ItemCreationDto item,
                                             @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ updateItem: itemId={}, item={}, userId={}", itemId, item, userId);
        ResponseEntity<Object> updatedItem = itemClient.updateItem(itemId, item, userId);
        log.debug("- updateItem: {}", updatedItem);
        return updatedItem;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestBody @Valid CommentCreationDto comment,
                                                @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ createComment: comment={}, itemId={}, userId={}", comment, itemId, userId);
        ResponseEntity<Object> createdComment = itemClient.createComment(itemId, comment, userId);
        log.debug("- createComment: {}", createdComment);
        return createdComment;
    }
}