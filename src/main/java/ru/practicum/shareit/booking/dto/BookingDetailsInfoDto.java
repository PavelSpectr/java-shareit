package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDetailsInfoDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private BookerDto booker;
    private BookingStatus status;

    @RequiredArgsConstructor
    @Getter
    public static class ItemDto {
        private final long id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public static class BookerDto {
        private final long id;
        private final String name;
    }
}