package ru.practicum.shareit.booking.model;

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

    public String getName() {
        return name;
    }
}