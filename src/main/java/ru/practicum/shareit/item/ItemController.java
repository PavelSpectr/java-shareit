package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDetailsInfoDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDetailsInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDetailsInfoDto> getItemsByOwnerId(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long ownerId) {
        log.debug("+ getItemsByOwnerId: ownerId={}", ownerId);
        List<ItemDetailsInfoDto> items = itemService.getItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDetailsInfoDto)
                .collect(toList());
        log.debug("- getItemsByOwnerId: {}", items);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDetailsInfoDto getItemById(@PathVariable Long itemId,
                                          @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ getItemById: itemId={}", itemId);
        ItemDetailsInfoDto item = ItemMapper.toItemDetailsInfoDto(itemService.getItemById(itemId, userId));
        log.debug("- getItemById: {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDetailsInfoDto> searchItems(@RequestParam String text) {
        log.debug("+ searchItems: text={}", text);
        List<ItemDetailsInfoDto> items = itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDetailsInfoDto)
                .collect(toList());
        log.debug("- searchItems: {}", items);
        return items;
    }

    @PostMapping
    public ItemDetailsInfoDto createItem(@RequestBody @Validated(ValidationGroup.OnCreate.class) ItemCreationDto item,
                                         @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ createItem: item={}, userId={}", item, userId);

        Item createdItem = itemService.createItem(ItemMapper.toItem(item), userId);
        ItemDetailsInfoDto createdItemDto = ItemMapper.toItemDetailsInfoDto(createdItem);

        log.debug("- createItem: {}", createdItemDto);

        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDetailsInfoDto updateItem(@PathVariable Long itemId,
                                         @RequestBody @Validated(ValidationGroup.OnUpdate.class) ItemCreationDto item,
                                         @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ updateItem: itemId={}, item={}, userId={}", itemId, item, userId);

        item.setId(itemId);
        Item updatedItem = itemService.updateItem(ItemMapper.toItem(item), userId);
        ItemDetailsInfoDto updatedItemDto = ItemMapper.toItemDetailsInfoDto(updatedItem);

        log.debug("- updateItem: {}", updatedItemDto);

        return updatedItemDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDetailsInfoDto createComment(@PathVariable Long itemId,
                                               @RequestBody @Valid CommentCreationDto comment,
                                               @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ createComment: comment={}, itemId={}, userId={}", comment, itemId, userId);

        Comment createdComment = itemService.createComment(itemId, CommentMapper.toComment(comment), userId);
        CommentDetailsInfoDto createdCommentDto = CommentMapper.toCommentDetailsInfoDto(createdComment);

        log.debug("- createComment: {}", createdCommentDto);

        return createdCommentDto;
    }
}