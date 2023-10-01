package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnauthorizedAccessException;
import ru.practicum.shareit.item.dto.CommentDetailsInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь #" + itemId + " не найдена."));
        Long itemOwnerId = item.getOwner().getId();

        if (itemOwnerId.equals(userId)) {
            setLastAndNextBookingData(item);
        }

        setComments(item);

        return item;
    }

    @Override
    public List<Item> getItemsByOwnerId(Long ownerId, Pageable pageable) {
        List<Item> items = itemRepository.findItemsByOwnerIdOrderById(ownerId, pageable);

        setLastAndNextBookingData(items);
        setComments(items);

        return items;
    }

    @Override
    public List<Item> searchItems(String searchText, Pageable pageable) {
        return (!searchText.isBlank()) ? itemRepository.search(searchText, pageable) : Collections.emptyList();
    }

    @Override
    public Item createItem(Item item, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь #" + ownerId + " не найден."));

        item.setOwner(owner);

        if (item.getRequest() != null) {
            item.setRequest(itemRequestRepository.findById(item.getRequest().getId()).orElse(null));
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item, Long ownerId) {
        Long itemId = item.getId();
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        Item currentItem = getItemById(itemId, ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь #" + ownerId + " не найден."));

        if (!Objects.equals(currentItem.getOwner().getId(), ownerId)) {
            final String errorMessage = "Недостаточно прав доступа для изменения данных вещи #" + itemId + ".";
            throw new UnauthorizedAccessException(errorMessage);
        }

        item.setOwner(owner);

        if (Objects.isNull(itemName) || itemName.isBlank()) {
            item.setName(currentItem.getName());
        }

        if (Objects.isNull(itemDescription) || itemDescription.isBlank()) {
            item.setDescription(currentItem.getDescription());
        }

        if (Objects.isNull(item.getAvailable())) {
            item.setAvailable(currentItem.getAvailable());
        }

        return itemRepository.save(item);
    }

    @Override
    public Comment createComment(Long itemId, Comment comment, Long authorId) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь #" + authorId + " не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь #" + itemId + " не найдена."));

        boolean isAvailableForComment = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(authorId,
                itemId, currentDateTime, BookingStatus.APPROVED);

        if (!isAvailableForComment) {
            throw new ValidationException("Пользователь #" + authorId + " не брал в аренду вещь #" + itemId + " " +
                    "или срок аренды еще не завершен.");
        }

        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(currentDateTime);
        return commentRepository.save(comment);
    }

    private void setLastAndNextBookingData(List<Item> items) {
        Map<Item, List<Booking>> bookingsByItem = bookingRepository
                .findAllByItemInAndStatusOrderByStartDesc(items, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));
        LocalDateTime currentDateTime = LocalDateTime.now();

        items.forEach(item -> {
            List<Booking> bookings = bookingsByItem.getOrDefault(item, Collections.emptyList());

            Optional<Booking> lastBooking = bookings.stream()
                    .filter(b -> (!b.getStart().isAfter(currentDateTime)))
                    .findFirst();

            Optional<Booking> nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(currentDateTime))
                    .reduce((first, second) -> second);

            lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper.toBookingDatesDto(booking)));
            nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper.toBookingDatesDto(booking)));
        });
    }

    private void setLastAndNextBookingData(Item item) {
        Long itemId = item.getId();
        LocalDateTime currentDateTime = LocalDateTime.now();

        Optional<Booking> lastBooking = bookingRepository
                .findFirstBookingByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(itemId,
                        currentDateTime,
                        BookingStatus.REJECTED);

        Optional<Booking> nextBooking = bookingRepository
                .findFirstBookingByItemIdAndStartAfterAndStatusNotOrderByStart(itemId, currentDateTime,
                        BookingStatus.REJECTED);

        lastBooking.ifPresent(booking -> item.setLastBooking(BookingMapper.toBookingDatesDto(booking)));
        nextBooking.ifPresent(booking -> item.setNextBooking(BookingMapper.toBookingDatesDto(booking)));
    }

    private void setComments(List<Item> items) {
        Map<Item, List<Comment>> commentsByItem = commentRepository.findByItemInOrderByCreatedDesc(items)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        items.forEach(item -> {
            List<Comment> comments = commentsByItem.getOrDefault(item, Collections.emptyList());

            item.setComments(comments
                    .stream()
                    .map(CommentMapper::toCommentDetailsInfoDto)
                    .collect(Collectors.toList()));
        });
    }

    private void setComments(Item item) {
        List<CommentDetailsInfoDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDetailsInfoDto)
                .collect(Collectors.toList());

        item.setComments(comments);
    }
}