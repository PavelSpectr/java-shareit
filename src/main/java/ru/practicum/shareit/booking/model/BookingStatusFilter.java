package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum BookingStatusFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingStatusFilter> optionalStatus(String status) {
        return Optional.of(BookingStatusFilter.valueOf(status));
    }
}