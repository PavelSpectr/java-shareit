package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnauthorizedAccessException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для ItemServiceImpl")
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, itemRequestRepository,
                commentRepository);
    }

    @Test
    @DisplayName("Возвращать вещь по id")
    public void shouldReturnItemById() {
        Item item = generator.nextObject(Item.class);
        Long itemId = item.getId();

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Item resultItem = itemService.getItemById(itemId, null);

        Assertions.assertEquals(item, resultItem);
    }

    @Test
    @DisplayName("Бросить исключение при получении вещи с неверным ID")
    public void shouldThrowExceptionWhenGetItemWithInvalidId() {
        Long invalidItemId = 1L;

        Mockito.when(itemRepository.findById(Mockito.eq(invalidItemId))).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(invalidItemId, null)
        );

        Assertions.assertEquals("Вещь #" + invalidItemId + " не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Заполнять данные бронирования, если ID владельца вещи совпадает с пользователем")
    public void shouldSetBookingDataIfOwnerIdEqualsUserId() {
        User owner = generator.nextObject(User.class);
        Booking booking = generator.nextObject(Booking.class);
        BookingDatesDto bookingDatesDto = BookingMapper.toBookingDatesDto(booking);
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        Long itemId = item.getId();

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Mockito.when(bookingRepository
                        .findFirstBookingByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(Mockito.eq(itemId),
                                Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                .thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository
                        .findFirstBookingByItemIdAndStartAfterAndStatusNotOrderByStart(Mockito.eq(itemId),
                                Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                .thenReturn(Optional.of(booking));

        Item resultItem = itemService.getItemById(itemId, owner.getId());

        Assertions.assertEquals(bookingDatesDto, resultItem.getLastBooking());
        Assertions.assertEquals(bookingDatesDto, resultItem.getNextBooking());
    }

    @Test
    @DisplayName("Возвращать список вещей по ID владельца")
    public void shouldReturnItemsByOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Item> items = generator.objects(Item.class, 3)
                .peek(item -> item.setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(itemRepository
                        .findItemsByOwnerIdOrderById(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);

        List<Item> resultItems = itemService.getItemsByOwnerId(owner.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(items.size(), resultItems.size());
        Assertions.assertEquals(items, resultItems);
    }

    @Test
    @DisplayName("Возвращать список вещей по тексту поискового запроса")
    public void shouldReturnItemsByText() {
        String searchText = "поисковая фраза";
        List<Item> items = generator.objects(Item.class, 3).collect(Collectors.toList());
        items.get(0).setName("Название с " + searchText);
        items.get(1).setDescription("Описание с " + searchText);

        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(items);

        List<Item> resultItems = itemService.searchItems(searchText, Pageable.ofSize(10));

        Assertions.assertEquals(items.size(), resultItems.size());
        Assertions.assertEquals(items, resultItems);
    }

    @Test
    @DisplayName("Возвращать пустой список при поиске вещей с пустым поисковым запросом")
    public void shouldReturnEmptyListWhenSearchingItemsWithTextIsBlank() {
        String emptySearchText = "";

        List<Item> resultItems = itemService.searchItems(emptySearchText, Pageable.ofSize(10));

        Assertions.assertEquals(Collections.emptyList(), resultItems);
        Mockito.verify(itemRepository, Mockito.never()).search(Mockito.anyString(), Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Создать и вернуть вещь")
    public void shouldCreateAndReturnItem() {
        User owner = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);

        Mockito.when(itemRepository.save(Mockito.eq(item))).thenReturn(item);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(owner));

        Item createdItem = itemService.createItem(item, owner.getId());

        Assertions.assertEquals(item, createdItem);
        Assertions.assertEquals(owner, createdItem.getOwner());
        Mockito.verify(itemRepository).save(Mockito.eq(item));
    }

    @Test
    @DisplayName("Бросить исключение при добавлении вещи с неверным ID владельца вещи")
    public void shouldThrowExceptionWhenCreatingItemWithInvalidOwnerId() {
        Long invalidOwnerId = 1L;
        Item item = generator.nextObject(Item.class);

        Mockito.when(userRepository.findById(Mockito.eq(invalidOwnerId))).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(item, invalidOwnerId)
        );

        Assertions.assertEquals("Пользователь #" + invalidOwnerId + " не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Обновить и вернуть вещь")
    public void shouldUpdateAndReturnItem() {
        Long ownerId = 1L;
        Long itemId = 1L;

        User owner = generator.nextObject(User.class);
        owner.setId(ownerId);

        Item currentItem = generator.nextObject(Item.class);
        currentItem.setId(itemId);
        currentItem.setOwner(owner);
        currentItem.setAvailable(false);

        Item newItem = generator.nextObject(Item.class);
        newItem.setId(itemId);
        newItem.setName("Новое наименование");
        newItem.setDescription("Новое описание");
        newItem.setAvailable(true);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(currentItem));
        Mockito.when(userRepository.findById(Mockito.eq(ownerId))).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.eq(newItem))).thenReturn(newItem);

        Item updatedItem = itemService.updateItem(newItem, ownerId);

        Assertions.assertEquals(newItem, updatedItem);
        Assertions.assertEquals(newItem.getName(), updatedItem.getName());
        Assertions.assertEquals(newItem.getDescription(), updatedItem.getDescription());
        Assertions.assertEquals(newItem.getAvailable(), updatedItem.getAvailable());

        Mockito.verify(userRepository).findById(ownerId);
        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(itemRepository).save(updatedItem);
    }

    @Test
    @DisplayName("Бросить исключение при обновлении вещи с неверным ID владельца вещи")
    public void shouldThrowExceptionWhenUpdatingItemWithInvalidOwnerId() {
        Long invalidOwnerId = 1L;
        Item item = generator.nextObject(Item.class);

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(Mockito.eq(invalidOwnerId))).thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item, invalidOwnerId)
        );

        Assertions.assertEquals("Пользователь #" + invalidOwnerId + " не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Бросить исключение при обновлении вещи с недостаточными правами доступа")
    public void shouldThrowExceptionWhenUpdatingItemWithUserIdIsNotEqualOwnerId() {
        Long userId = 2L;
        Long ownerId = 1L;
        Long itemId = 1L;

        User user = generator.nextObject(User.class);
        user.setId(userId);

        User owner = generator.nextObject(User.class);
        owner.setId(ownerId);

        Item currentItem = generator.nextObject(Item.class);
        currentItem.setId(itemId);
        currentItem.setOwner(owner);

        Item newItem = generator.nextObject(Item.class);
        newItem.setId(itemId);

        Mockito.when(itemRepository.findById(Mockito.eq(itemId))).thenReturn(Optional.of(currentItem));
        Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.of(user));

        final UnauthorizedAccessException exception = Assertions.assertThrows(UnauthorizedAccessException.class,
                () -> itemService.updateItem(newItem, userId)
        );

        Assertions.assertEquals("Недостаточно прав доступа для изменения данных вещи #" + itemId + ".",
                exception.getMessage());
    }

    @Test
    @DisplayName("Сохранять текущие значения полей при обновлении объектом с пустыми полями")
    public void shouldSaveCurrentValuesWhenUpdatingItemWithEmptyValues() {
        Long ownerId = 1L;
        Long itemId = 1L;

        User owner = generator.nextObject(User.class);
        owner.setId(ownerId);

        Item currentItem = generator.nextObject(Item.class);
        currentItem.setId(itemId);
        currentItem.setOwner(owner);
        currentItem.setAvailable(false);

        Item newItem = generator.nextObject(Item.class);
        newItem.setId(itemId);
        newItem.setName(null);
        newItem.setDescription(null);
        newItem.setAvailable(null);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(currentItem));
        Mockito.when(userRepository.findById(Mockito.eq(ownerId))).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.save(Mockito.eq(newItem))).thenReturn(newItem);

        Item updatedItem = itemService.updateItem(newItem, ownerId);

        Assertions.assertEquals(currentItem.getName(), updatedItem.getName());
        Assertions.assertEquals(currentItem.getDescription(), updatedItem.getDescription());
        Assertions.assertEquals(currentItem.getAvailable(), updatedItem.getAvailable());

        Mockito.verify(userRepository).findById(ownerId);
        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(itemRepository).save(updatedItem);
    }

    @Test
    @DisplayName("Создать и вернуть комментарий")
    public void shouldCreateAndReturnComment() {
        User author = generator.nextObject(User.class);
        Long authorId = author.getId();

        Item item = generator.nextObject(Item.class);
        Long itemId = item.getId();

        Comment comment = generator.nextObject(Comment.class);

        Mockito.when(userRepository.findById(Mockito.eq(authorId))).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.eq(itemId))).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                Mockito.eq(authorId), Mockito.eq(itemId), Mockito.any(LocalDateTime.class),
                Mockito.any(BookingStatus.class)
        )).thenReturn(true);
        Mockito.when(commentRepository.save(Mockito.eq(comment))).thenReturn(comment);

        Comment createdComment = itemService.createComment(itemId, comment, authorId);

        Assertions.assertEquals(comment, createdComment);
        Mockito.verify(commentRepository).save(Mockito.eq(comment));
    }

    @Test
    @DisplayName("Бросить исключение при создании комментария пользователем который не арендовал вещь " +
            "или срок аренды еще не завершен")
    public void shouldThrowExceptionWhenCreatingCommentWithAuthorIsNotBookerAndBookingNotIsPast() {
        User author = generator.nextObject(User.class);
        Long authorId = author.getId();

        Item item = generator.nextObject(Item.class);
        Long itemId = item.getId();

        Comment comment = generator.nextObject(Comment.class);

        Mockito.when(userRepository.findById(Mockito.eq(authorId))).thenReturn(Optional.of(author));
        Mockito.when(itemRepository.findById(Mockito.eq(itemId))).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                Mockito.eq(authorId), Mockito.eq(itemId), Mockito.any(LocalDateTime.class),
                Mockito.any(BookingStatus.class)
        )).thenReturn(false);

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemService.createComment(itemId, comment, authorId)
        );

        Assertions.assertEquals("Пользователь #" + authorId + " не брал в аренду вещь #" + itemId + " " +
                        "или срок аренды еще не завершен.",
                exception.getMessage());
    }
}