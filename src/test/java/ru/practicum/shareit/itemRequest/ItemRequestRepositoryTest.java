package ru.practicum.shareit.itemRequest;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DisplayName("Тесты для ItemRequestRepository")
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Поиск запросов на вещи по id пользователя с сортировкой по дате создания")
    public void shouldFindItemRequestsByRequestorIdOrderByCreatedDesc() {
        User requestor = createUser();

        ItemRequest itemRequestOne = createItemRequest(requestor);
        ItemRequest itemRequestTwo = createItemRequest(requestor);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findItemRequestsByRequestorIdOrderByCreatedDesc(requestor.getId());

        Assertions.assertEquals(2, itemRequests.size());
        Assertions.assertEquals(itemRequestTwo.getId(), itemRequests.get(0).getId());
        Assertions.assertEquals(itemRequestOne.getId(), itemRequests.get(1).getId());
    }

    @Test
    @DisplayName("Поиск запросов на вещи, исключая определенного пользователя, с сортировкой по дате создания")
    public void shouldFindByRequestorIdNotOrderByCreatedDesc() {
        User requestor = createUser();
        User anotherRequestor = createUser();

        createItemRequest(requestor);
        createItemRequest(requestor);
        ItemRequest itemRequest = createItemRequest(anotherRequestor);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findByRequestorIdNotOrderByCreatedDesc(requestor.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(1, itemRequests.size());
        Assertions.assertEquals(itemRequest.getId(), itemRequests.get(0).getId());
    }

    private User createUser() {
        User requestor = generator.nextObject(User.class);
        return userRepository.save(requestor);
    }

    private ItemRequest createItemRequest(User requestor) {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }
}