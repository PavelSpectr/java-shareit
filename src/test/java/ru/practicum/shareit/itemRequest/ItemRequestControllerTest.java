package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.itemRequest.ItemRequestController;
import ru.practicum.shareit.itemRequest.dto.ItemRequestCreationDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDetailsInfoDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Возвращать список заявок по ID заявителя")
    public void shouldReturnRequestsByRequestorId() throws Exception {
        Long requestorId = 1L;
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 2).collect(Collectors.toList());

        List<ItemRequestDetailsInfoDto> expectedItemRequests = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDetailsInfoDto)
                .collect(Collectors.toList());

        Mockito.when(itemRequestService.getItemRequestsByRequestorId(Mockito.anyLong()))
                .thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedItemRequests.get(0).getId().intValue()))
                .andExpect(jsonPath("$[0].description").value(expectedItemRequests.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(expectedItemRequests.get(1).getId().intValue()))
                .andExpect(jsonPath("$[1].description").value(expectedItemRequests.get(1).getDescription()));

        Mockito.verify(itemRequestService).getItemRequestsByRequestorId(Mockito.anyLong());
    }

    @Test
    @DisplayName("Возвращать список всех заявок")
    public void shouldReturnRequests() throws Exception {
        Long userId = 1L;
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 2)
                .collect(Collectors.toList());

        List<ItemRequestDetailsInfoDto> expectedItemRequests = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDetailsInfoDto)
                .collect(Collectors.toList());

        Mockito.when(itemRequestService.getItemRequests(Mockito.anyLong(), Mockito.any(PageRequest.class)))
                .thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedItemRequests.get(0).getId().intValue()))
                .andExpect(jsonPath("$[0].description").value(expectedItemRequests.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(expectedItemRequests.get(1).getId().intValue()))
                .andExpect(jsonPath("$[1].description").value(expectedItemRequests.get(1).getDescription()));

        Mockito.verify(itemRequestService).getItemRequests(Mockito.anyLong(), Mockito.any(PageRequest.class));
    }

    @Test
    @DisplayName("Возвращать заявку по id")
    public void shouldReturnRequestById() throws Exception {
        Long requestId = 1L;
        Long userId = 1L;
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);

        ItemRequestDetailsInfoDto expectedItemRequest = ItemRequestMapper.toItemRequestDetailsInfoDto(itemRequest);

        Mockito.when(itemRequestService.getItemRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequest);

        mockMvc.perform(get("/requests/{itemRequestId}", requestId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedItemRequest.getId().intValue()))
                .andExpect(jsonPath("$.description").value(expectedItemRequest.getDescription()));

        Mockito.verify(itemRequestService).getItemRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("Создание и возвращение заявки")
    public void shouldCreateAndReturnRequest() throws Exception {
        Long userId = 1L;
        ItemRequestCreationDto request = generator.nextObject(ItemRequestCreationDto.class);

        Mockito.when(itemRequestService.createItemRequest(Mockito.any(ItemRequest.class), Mockito.anyLong()))
                .thenReturn(ItemRequestMapper.toItemRequest(request));

        mockMvc.perform(post("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.getDescription()));

        Mockito.verify(itemRequestService).createItemRequest(Mockito.any(ItemRequest.class), Mockito.anyLong());
    }
}