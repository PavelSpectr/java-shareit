package ru.practicum.shareit.booking.model;

import lombok.Getter;

@Getter
public enum BookingStatus {
    WAITING("ожидает одобрения"),
    APPROVED("подтверждено владельцем"),
    REJECTED("отклонено владельцем"),
    CANCELED("отменено создателем");

    private final String name;

    BookingStatus(String name) {
        this.name = name;
    }

}