package ru.practicum.shareit.booking.model;

import lombok.Getter;

@Getter
public enum BookingStatusFilter {
    ALL("все"),
    CURRENT("текущие"),
    PAST("завершенные"),
    FUTURE("будущие"),
    WAITING("ожидающие подтверждения"),
    REJECTED("отклоненные");

    private final String name;

    BookingStatusFilter(String name) {
        this.name = name;
    }

}