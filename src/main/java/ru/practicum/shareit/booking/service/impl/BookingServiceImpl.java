package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusFilter;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<Booking> getBookingsByBookerId(long bookerId, String statusFilter) {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("Пользователь #" + bookerId + " не найден.");
        }

        try {
            BookingStatusFilter bookingStatusFilter = BookingStatusFilter.optionalStatus(statusFilter).orElseThrow(() ->
                    new ValidationException("Unknown state: UNSUPPORTED_STATUS"));

            switch (bookingStatusFilter) {
                case ALL:
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                            currentDateTime,
                            currentDateTime);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, currentDateTime);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, currentDateTime);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                    break;
                //Не совсем понимаю, как это работает, но return bookings, в случае удаления default, требует инициализации
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByItemOwnerId(long itemOwnerId, String statusFilter) {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!userRepository.existsById(itemOwnerId)) {
            throw new NotFoundException("Пользователь #" + itemOwnerId + " не найден.");
        }

        try {
            BookingStatusFilter bookingStatusFilter = BookingStatusFilter.optionalStatus(statusFilter).orElseThrow(() ->
                    new ValidationException("Unknown state: UNSUPPORTED_STATUS"));
            switch (bookingStatusFilter) {
                case ALL:
                    bookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(itemOwnerId);
                    break;
                case CURRENT:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(itemOwnerId,
                            currentDateTime,
                            currentDateTime);
                    break;
                case PAST:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(itemOwnerId, currentDateTime);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(itemOwnerId, currentDateTime);
                    break;
                case WAITING:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(itemOwnerId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(itemOwnerId, BookingStatus.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование #" + bookingId + " не найдено."));
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(itemOwnerId)) {
            throw new NotFoundException("Недостаточно прав доступа для " +
                    "получения данных бронирования #" + bookingId + ".");
        }

        return booking;
    }

    @Override
    public Booking createBooking(Booking booking, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь #" + bookerId + " не найден."));

        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь #" + itemId + " не найдена."));

        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь #" + itemId + " недоступна для бронирования."));
        }

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(item.getId(), booking.getStart(), booking.getEnd());
        if (!overlappingBookings.isEmpty()) {
            throw new ValidationException("Невозможно создать бронирование. Уже существуют бронирования, которые пересекаются по времени.");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Невозможно выполнить бронирование вещи #" + itemId + ", " +
                    "так как владелец вещи и пользователь совпадают.");
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveOrRejectBooking(Long bookingId, boolean isApproved, Long userId) {
        Booking booking = getBookingById(bookingId, userId);
        Long itemOwnerId = booking.getItem().getOwner().getId();
        BookingStatus currentStatus = booking.getStatus();

        if (!itemOwnerId.equals(userId)) {
            throw new NotFoundException("Недостаточно прав доступа для " +
                    "изменения статуса бронирования #" + bookingId + ".");
        }

        if (!currentStatus.equals(BookingStatus.WAITING)) {
            throw new ValidationException("Подтверждение или отклонение запроса на бронирование невозможно, " +
                    "так как бронирование #" + bookingId + " имеет статус " + currentStatus);
        }

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }
}