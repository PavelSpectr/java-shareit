package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@DataJpaTest
@DisplayName("Тесты для ItemRepository")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Поиск вещей по id владельца и сортировка по id")
    public void shouldFindItemsByOwnerIdOrderById() {
        User owner = createUser();
        Item itemOne = createItem(owner);
        Item itemTwo = createItem(owner);

        List<Item> items = itemRepository.findItemsByOwnerIdOrderById(owner.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(itemOne.getId(), items.get(0).getId());
        Assertions.assertEquals(itemTwo.getId(), items.get(1).getId());
    }

    @Test
    @DisplayName("Поиск вещей по запросу")
    public void shouldFindByRequestIn() {
        User owner = createUser();
        User requestor = createUser();
        ItemRequest itemRequestOne = createItemRequest(requestor);
        ItemRequest itemRequestTwo = createItemRequest(requestor);
        Item itemOne = createItem(owner, itemRequestOne);
        Item itemTwo = createItem(owner, itemRequestTwo);

        List<Item> items = itemRepository.findByRequestIn(Arrays.asList(itemRequestOne, itemRequestTwo));

        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(itemOne.getId(), items.get(0).getId());
        Assertions.assertEquals(itemTwo.getId(), items.get(1).getId());
    }

    @Test
    @DisplayName("Поиск вещей по id запроса")
    public void shouldFindAllByRequestId() {
        User owner = createUser();
        User requestor = createUser();
        ItemRequest request = createItemRequest(requestor);
        Item itemOne = createItem(owner, request);
        Item itemTwo = createItem(owner, request);

        List<Item> items = itemRepository.findAllByRequestId(request.getId());

        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(itemOne.getId(), items.get(0).getId());
        Assertions.assertEquals(itemTwo.getId(), items.get(1).getId());
    }

    @Test
    @DisplayName("Поиск вещей по тексту")
    public void shouldSearch() {
        String searchText = "поисковая фраза";
        User owner = createUser();

        Item itemOne = createItem(owner);
        itemOne.setName("Название с " + searchText);
        itemRepository.save(itemOne);

        Item itemTwo = createItem(owner);
        itemTwo.setDescription("Описание с " + searchText);
        itemRepository.save(itemTwo);

        List<Item> items = itemRepository.search(searchText, Pageable.ofSize(10));

        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(itemOne.getId(), items.get(0).getId());
        Assertions.assertEquals(itemTwo.getId(), items.get(1).getId());
    }

    private User createUser() {
        User owner = generator.nextObject(User.class);
        return userRepository.save(owner);
    }

    private ItemRequest createItemRequest(User requestor) {
        ItemRequest request = generator.nextObject(ItemRequest.class);
        request.setRequestor(requestor);
        return itemRequestRepository.save(request);
    }

    private Item createItem(User owner, ItemRequest itemRequest) {
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return itemRepository.save(item);
    }

    private Item createItem(User owner) {
        return createItem(owner, null);
    }
}