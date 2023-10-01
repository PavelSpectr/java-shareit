package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDetailsInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handlers.HeaderConstants;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Возвращать список бронирований по id бронирующего")
    public void shouldReturnBookingsByBookerId() throws Exception {
        Long bookerId = 1L;
        List<Booking> bookings = generator.objects(Booking.class, 2)
                .collect(Collectors.toList());

        List<BookingDetailsInfoDto> expectedBookings = bookings.stream()
                .map(BookingMapper::toBookingDetailsDto)
                .collect(Collectors.toList());

        Mockito.when(bookingService.getBookingsByBookerId(Mockito.anyLong(), Mockito.anyString(),
                        Mockito.any(PageRequest.class)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(null))
                        .header(HeaderConstants.X_SHARER_USER_ID, bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedBookings.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value((expectedBookings.get(1).getId())));

        Mockito.verify(bookingService).getBookingsByBookerId(Mockito.anyLong(), Mockito.anyString(), Mockito.any(PageRequest.class));
    }

    @Test
    @DisplayName("Возвращать список бронирований по id владельца вещи")
    public void shouldReturnBookingsByItemOwnerId() throws Exception {
        Long ownerId = 1L;
        List<Booking> bookings = generator.objects(Booking.class, 2).collect(Collectors.toList());

        List<BookingDetailsInfoDto> expectedBookings =
                bookings.stream()
                        .map(BookingMapper::toBookingDetailsDto)
                        .collect(Collectors.toList());

        Mockito.when(bookingService.getBookingsByItemOwnerId(Mockito.anyLong(), Mockito.anyString(),
                        Mockito.any(PageRequest.class)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .content(objectMapper.writeValueAsString(null))
                        .header(HeaderConstants.X_SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(expectedBookings.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(expectedBookings.get(1).getId()));

        Mockito.verify(bookingService).getBookingsByItemOwnerId(Mockito.anyLong(),
                Mockito.anyString(), Mockito.any(PageRequest.class));
    }

    @Test
    @DisplayName("Возвращать бронирование по id")
    public void shouldReturnBookingById() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking booking = generator.nextObject(Booking.class);

        BookingDetailsInfoDto expectedBooking = BookingMapper.toBookingDetailsDto(booking);

        Mockito.when(bookingService.getBookingById(Mockito.anyLong(),
                        Mockito.anyLong()))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .content(objectMapper.writeValueAsString(null))
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBooking.getId()));

        Mockito.verify(bookingService).getBookingById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("Создать и вернуть новое бронирование")
    public void shouldCreateAndReturnBooking() throws Exception {
        Long userId = 1L;
        User booker = generator.nextObject(User.class);
        BookingCreationDto bookingDto = generator.nextObject(BookingCreationDto.class);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);

        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class),
                        Mockito.anyLong()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));

        Mockito.verify(bookingService).createBooking(Mockito.any(Booking.class),
                Mockito.anyLong());
    }

    @Test
    @DisplayName("Одобрить или отклонить бронирование, и вернуть обновленное бронирование")
    public void shouldApproveOrRejectBooking() throws Exception {
        Long bookingId = 1L;
        boolean isApproved = true;

        Booking booking = generator.nextObject(Booking.class);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDetailsInfoDto expectedBooking = BookingMapper.toBookingDetailsDto(booking);

        Mockito.when(bookingService.approveOrRejectBooking(Mockito.anyLong(),
                        Mockito.anyBoolean(),
                        Mockito.anyLong()))
                .thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.X_SHARER_USER_ID, booking.getBooker().getId())
                        .param("approved", Boolean.toString(isApproved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedBooking.getId()))
                .andExpect(jsonPath("$.status").value(expectedBooking.getStatus().toString()));

        Mockito.verify(bookingService).approveOrRejectBooking(Mockito.anyLong(),
                Mockito.anyBoolean(), Mockito.anyLong());
    }
}