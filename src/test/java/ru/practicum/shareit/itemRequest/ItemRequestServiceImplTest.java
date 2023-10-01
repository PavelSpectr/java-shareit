package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;
import ru.practicum.shareit.itemRequest.service.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для ItemRequestServiceImpl")
class ItemRequestServiceImplTest {
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    @DisplayName("Возвращает запрос на вещь по id")
    public void shouldReturnItemRequestById() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        Long itemRequestId = itemRequest.getId();
        Long userId = 1L;

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest resultItemRequest = itemRequestService.getItemRequestById(itemRequestId, userId);

        Assertions.assertEquals(itemRequest, resultItemRequest);
    }

    @Test
    @DisplayName("Бросить исключение при получении запроса вещи с неверным id пользователя")
    public void shouldThrowExceptionWhenGetItemRequestWithInvalidUserId() {
        Long invalidUserId = 1L;
        Long requestId = 1L;

        Mockito.when(userRepository.existsById(invalidUserId)).thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(requestId, invalidUserId));

        Assertions.assertEquals("Пользователь #" + invalidUserId + " не найден.", exception.getMessage());
    }

    @Test
    @DisplayName("Возвращать список запросов на вещь")
    public void shouldReturnItemRequests() {
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 3)
                .collect(Collectors.toList());
        Long userId = 1L;

        Mockito.when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(itemRequests);

        List<ItemRequest> resultItemRequests = itemRequestService.getItemRequests(userId,
                Pageable.ofSize(10));

        Assertions.assertEquals(itemRequests.size(), resultItemRequests.size());
        Assertions.assertEquals(itemRequests, resultItemRequests);
    }

    @Test
    @DisplayName("Возвращать список запросов на вещь по id пользователя")
    public void shouldReturnItemRequestsByRequestorId() {
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 3)
                .collect(Collectors.toList());
        Long requestorId = 1L;

        Mockito.when(userRepository.existsById(Mockito.eq(requestorId))).thenReturn(true);
        Mockito.when(itemRequestRepository.findItemRequestsByRequestorIdOrderByCreatedDesc(Mockito.anyLong()))
                .thenReturn(itemRequests);

        List<ItemRequest> resultItemRequests = itemRequestService.getItemRequestsByRequestorId(requestorId);

        Assertions.assertEquals(itemRequests.size(), resultItemRequests.size());
        Assertions.assertEquals(itemRequests, resultItemRequests);
    }

    @Test
    @DisplayName("Бросить исключение при получении списка запросов вещей с неверным id заявителя")
    public void shouldThrowExceptionWhenGetItemRequestsByRequestorIdWithInvalidUserId() {
        Long invalidUserId = 1L;

        Mockito.when(userRepository.existsById(invalidUserId)).thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByRequestorId(invalidUserId));

        Assertions.assertEquals("Пользователь #" + invalidUserId + " не найден.", exception.getMessage());
    }


    @Test
    @DisplayName("Создать и вернуть запрос на вещь")
    public void shouldCreateAndReturnItemRequest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        Long requestorId = itemRequest.getId();

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest.getRequestor()));
        Mockito.when(itemRequestRepository.save(Mockito.eq(itemRequest)))
                .thenReturn(itemRequest);

        ItemRequest createdItemRequest = itemRequestService.createItemRequest(itemRequest, requestorId);

        Assertions.assertEquals(itemRequest, createdItemRequest);
    }
}