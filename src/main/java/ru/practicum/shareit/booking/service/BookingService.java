package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> getBookingsByBookerId(long bookerId, String statusFilter);

    List<Booking> getBookingsByItemOwnerId(long itemOwnerId, String statusFilter);

    Booking getBookingById(Long bookingId, Long userId);

    Booking createBooking(Booking booking, Long bookerId);

    Booking approveOrRejectBooking(Long bookingId, boolean isApproved, Long userId);
}