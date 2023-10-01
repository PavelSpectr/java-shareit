package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDetailsInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Возвращать список вещей по ID владельца")
    public void shouldReturnItemsByOwnerId() throws Exception {
        Long ownerId = 1L;
        List<Item> items = generator.objects(Item.class, 2).collect(Collectors.toList());

        List<ItemDetailsInfoDto> expectedItems = items.stream()
                .map(ItemMapper::toItemDetailsInfoDto)
                .collect(Collectors.toList());

        Mockito.when(itemService.getItemsByOwnerId(Mockito.anyLong(), Mockito.any(PageRequest.class)))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(null))
                        .header(HeaderConstants.X_SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedItems.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(expectedItems.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(expectedItems.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(expectedItems.get(0).getAvailable()))
                .andExpect(jsonPath("$[1].id").value(expectedItems.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(expectedItems.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(expectedItems.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(expectedItems.get(1).getAvailable()));

        Mockito.verify(itemService).getItemsByOwnerId(Mockito.anyLong(), Mockito.any(PageRequest.class));
    }

    @Test
    @DisplayName("Возвращать вещь по id")
    public void shouldReturnItemById() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        Item item = generator.nextObject(Item.class);

        ItemDetailsInfoDto expectedItem = ItemMapper.toItemDetailsInfoDto(item);

        Mockito.when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(null))
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItem.getId()))
                .andExpect(jsonPath("$.name").value(expectedItem.getName()))
                .andExpect(jsonPath("$.description").value(expectedItem.getDescription()))
                .andExpect(jsonPath("$.available").value(expectedItem.getAvailable()));

        Mockito.verify(itemService).getItemById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("Бросить исключение при получении вещи с неверным ID")
    public void shouldThrowExceptionWhenGetItemWithInvalidId() throws Exception {
        Long invalidItemId = 99L;
        Long userId = 1L;

        Mockito.when(itemService.getItemById(invalidItemId, userId)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", invalidItemId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Возвращать список вещей по тексту поискового запроса")
    public void shouldReturnItemsByText() throws Exception {
        String searchText = "поисковая фраза";
        List<Item> items = generator.objects(Item.class, 2).collect(Collectors.toList());
        items.get(0).setName("Название с " + searchText);
        items.get(0).setDescription("Описание с " + searchText);

        List<ItemDetailsInfoDto> expectedItems = items.stream()
                .map(ItemMapper::toItemDetailsInfoDto)
                .collect(Collectors.toList());

        Mockito.when(itemService.searchItems(Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedItems.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(expectedItems.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(expectedItems.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(expectedItems.get(0).getAvailable()))
                .andExpect(jsonPath("$[1].id").value(expectedItems.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(expectedItems.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(expectedItems.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(expectedItems.get(1).getAvailable()));

        Mockito.verify(itemService).searchItems(Mockito.anyString(), Mockito.any(PageRequest.class));
    }


    @Test
    @DisplayName("Создать и вернуть вещь")
    public void shouldCreateAndReturnItem() throws Exception {
        Long userId = 1L;
        ItemCreationDto item = generator.nextObject(ItemCreationDto.class);

        Mockito.when(itemService.createItem(Mockito.any(Item.class), Mockito.anyLong()))
                .thenReturn(ItemMapper.toItem(item));

        mockMvc.perform(post("/items")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));

        Mockito.verify(itemService).createItem(Mockito.any(Item.class), Mockito.anyLong());
    }

    @Test
    @DisplayName("Обновить и вернуть вещь")
    public void shouldUpdateAndReturnItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemCreationDto item = generator.nextObject(ItemCreationDto.class);

        Mockito.when(itemService.updateItem(Mockito.any(Item.class), Mockito.anyLong()))
                .thenReturn(ItemMapper.toItem(item));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));

        Mockito.verify(itemService).updateItem(Mockito.any(Item.class), Mockito.anyLong());
    }

    @Test
    @DisplayName("Создать и вернуть комментарий")
    public void shouldCreateAndReturnComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        User user = generator.nextObject(User.class);
        CommentCreationDto commentCreationDto = generator.nextObject(CommentCreationDto.class);
        Comment comment = CommentMapper.toComment(commentCreationDto);
        comment.setAuthor(user);

        Mockito.when(itemService.createComment(Mockito.anyLong(), Mockito.any(Comment.class), Mockito.anyLong()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(comment.getText()));

        Mockito.verify(itemService).createComment(Mockito.anyLong(), Mockito.any(Comment.class), Mockito.anyLong());
    }
}