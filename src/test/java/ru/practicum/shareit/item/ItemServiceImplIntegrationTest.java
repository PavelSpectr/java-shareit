package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("Интеграционные тесты для ItemServiceImpl")
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DirtiesContext
    @DisplayName("Возвращать вещь по id")
    public void shouldReturnItemById() {
        User owner = createUser();
        Item item = createItem(owner);

        Item resultItem = itemService.getItemById(item.getId(), null);

        Assertions.assertNotNull(resultItem.getId());
        Assertions.assertEquals(item.getName(), resultItem.getName());
        Assertions.assertEquals(item.getDescription(), resultItem.getDescription());
        Assertions.assertEquals(item.getAvailable(), resultItem.getAvailable());
        Assertions.assertEquals(item.getOwner().getId(), resultItem.getOwner().getId());
        Assertions.assertNull(resultItem.getRequest());
        Assertions.assertNull(resultItem.getLastBooking());
        Assertions.assertNull(resultItem.getNextBooking());
    }

    @Test
    @DirtiesContext
    @DisplayName("Возвращать список вещей по ID владельца")
    public void shouldReturnItemsByOwnerId() {
        User owner = createUser();
        User booker = createUser();

        Item itemOne = createItem(owner);
        Booking lastBookingOne = pastBooking(itemOne, booker);
        Booking nextBookingOne = futureBooking(itemOne, booker);
        Comment commentOne = createComment(itemOne, booker);

        Item itemTwo = createItem(owner);
        Booking lastBookingTwo = pastBooking(itemTwo, booker);
        Booking nextBookingTwo = futureBooking(itemTwo, booker);
        Comment commentTwo = createComment(itemTwo, booker);

        List<Item> resultItems = itemService.getItemsByOwnerId(owner.getId(), Pageable.ofSize(10));

        Assertions.assertEquals(2, resultItems.size());

        Assertions.assertEquals(1, resultItems.get(0).getComments().size());
        Assertions.assertEquals(itemOne.getId(), resultItems.get(0).getId());
        Assertions.assertEquals(itemOne.getName(), resultItems.get(0).getName());
        Assertions.assertEquals(itemOne.getDescription(), resultItems.get(0).getDescription());
        Assertions.assertEquals(itemOne.getAvailable(), resultItems.get(0).getAvailable());
        Assertions.assertEquals(itemOne.getOwner().getId(), resultItems.get(0).getOwner().getId());
        Assertions.assertEquals(itemOne.getRequest(), resultItems.get(0).getRequest());
        Assertions.assertEquals(lastBookingOne.getId(), resultItems.get(0).getLastBooking().getId());
        Assertions.assertEquals(nextBookingOne.getId(), resultItems.get(0).getNextBooking().getId());
        Assertions.assertEquals(commentOne.getId(), resultItems.get(0).getComments().get(0).getId());

        Assertions.assertEquals(1, resultItems.get(1).getComments().size());
        Assertions.assertEquals(itemTwo.getId(), resultItems.get(1).getId());
        Assertions.assertEquals(itemTwo.getName(), resultItems.get(1).getName());
        Assertions.assertEquals(itemTwo.getDescription(), resultItems.get(1).getDescription());
        Assertions.assertEquals(itemTwo.getAvailable(), resultItems.get(1).getAvailable());
        Assertions.assertEquals(itemTwo.getOwner().getId(), resultItems.get(1).getOwner().getId());
        Assertions.assertEquals(itemTwo.getRequest(), resultItems.get(1).getRequest());
        Assertions.assertEquals(lastBookingTwo.getId(), resultItems.get(1).getLastBooking().getId());
        Assertions.assertEquals(nextBookingTwo.getId(), resultItems.get(1).getNextBooking().getId());
        Assertions.assertEquals(commentTwo.getId(), resultItems.get(1).getComments().get(0).getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Возвращать список вещей по тексту поискового запроса")
    public void shouldReturnItemsByText() {
        String searchText = "поисковая фраза";
        User owner = createUser();

        Item itemOne = createItem(owner);
        itemOne.setName("Название с " + searchText);
        itemRepository.save(itemOne);

        Item itemTwo = createItem(owner);
        itemTwo.setDescription("Описание с " + searchText);
        itemRepository.save(itemTwo);

        List<Item> resultItems = itemService.searchItems(searchText, Pageable.ofSize(10));

        Assertions.assertEquals(2, resultItems.size());

        Assertions.assertEquals(itemOne.getId(), resultItems.get(0).getId());
        Assertions.assertEquals(itemOne.getName(), resultItems.get(0).getName());
        Assertions.assertEquals(itemOne.getDescription(), resultItems.get(0).getDescription());
        Assertions.assertEquals(itemOne.getAvailable(), resultItems.get(0).getAvailable());
        Assertions.assertEquals(itemOne.getOwner().getId(), resultItems.get(0).getOwner().getId());

        Assertions.assertEquals(itemTwo.getId(), resultItems.get(1).getId());
        Assertions.assertEquals(itemTwo.getName(), resultItems.get(1).getName());
        Assertions.assertEquals(itemTwo.getDescription(), resultItems.get(1).getDescription());
        Assertions.assertEquals(itemTwo.getAvailable(), resultItems.get(1).getAvailable());
        Assertions.assertEquals(itemTwo.getOwner().getId(), resultItems.get(1).getOwner().getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Создать и вернуть вещь")
    public void shouldCreateAndReturnItem() {
        User owner = createUser();
        Item item = generator.nextObject(Item.class);
        item.setOwner(owner);
        item.setAvailable(true);

        Item createdItem = itemService.createItem(item, owner.getId());

        Assertions.assertNotNull(createdItem.getId());
        Assertions.assertEquals(item.getName(), createdItem.getName());
        Assertions.assertEquals(item.getDescription(), createdItem.getDescription());
        Assertions.assertTrue(createdItem.getAvailable());
    }

    @Test
    @DirtiesContext
    @DisplayName("Обновить и вернуть вещь")
    public void shouldUpdateAndReturnItem() {
        User owner = createUser();
        Item currentItem = createItem(owner);

        Item newItem = generator.nextObject(Item.class);
        newItem.setId(currentItem.getId());
        newItem.setName("Новое наименование");
        newItem.setDescription("Новое описание");
        newItem.setAvailable(true);
        newItem.setRequest(null);

        Item updatedItem = itemService.updateItem(newItem, owner.getId());

        Assertions.assertEquals(newItem.getId(), updatedItem.getId());
        Assertions.assertEquals(newItem.getName(), updatedItem.getName());
        Assertions.assertEquals(newItem.getDescription(), updatedItem.getDescription());
        Assertions.assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    @DirtiesContext
    @DisplayName("Создать и вернуть комментарий")
    public void shouldCreateAndReturnComment() {
        User author = createUser();
        User owner = createUser();
        Item item = createItem(owner);
        pastBooking(item, author);

        Comment comment = generator.nextObject(Comment.class);
        comment.setId(null);
        comment.setItem(item);
        comment.setAuthor(author);

        Comment createdComment = itemService.createComment(item.getId(), comment, author.getId());

        Assertions.assertNotNull(createdComment.getId());
        Assertions.assertNotNull(createdComment.getCreated());
        Assertions.assertEquals(comment.getText(), createdComment.getText());
        Assertions.assertEquals(comment.getItem().getId(), createdComment.getItem().getId());
        Assertions.assertEquals(comment.getAuthor().getId(), createdComment.getAuthor().getId());
    }

    private User createUser() {
        User owner = generator.nextObject(User.class);
        owner.setId(null);
        return userRepository.save(owner);
    }

    private Item createItem(User owner) {
        Item item = generator.nextObject(Item.class);
        item.setId(null);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setRequest(null);
        item.setComments(Collections.emptyList());

        return itemRepository.save(item);
    }

    private Booking pastBooking(Item item, User booker) {
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        return bookingRepository.save(booking);
    }

    private Booking futureBooking(Item item, User booker) {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        return bookingRepository.save(booking);
    }

    private Comment createComment(Item item, User author) {
        Comment comment = generator.nextObject(Comment.class);
        comment.setId(null);
        comment.setItem(item);
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

}