package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.validation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingCreationDto {
    private Long id;

    @FutureOrPresent(message = "Дата начала бронирования должна быть в будущем или настоящем")
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull(message = "Идентификатор вещи, которую бронирует пользователь не должен быть null")
    private Long itemId;
}