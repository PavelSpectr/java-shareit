package ru.practicum.shareit.booking.model;

import java.util.Optional;
import java.util.stream.Stream;

public enum BookingStatusFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingStatusFilter> optionalStatus(String status) {
        return Stream.of(values())
                .filter(filter -> filter.name().equalsIgnoreCase(status))
                .findFirst();
    }
}