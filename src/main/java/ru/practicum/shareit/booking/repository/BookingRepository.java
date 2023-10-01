package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                             LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start,
                                                                                 LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstBookingByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(Long itemId,
                                                                                         LocalDateTime start,
                                                                                         BookingStatus status);

    Optional<Booking> findFirstBookingByItemIdAndStartAfterAndStatusNotOrderByStart(Long itemId, LocalDateTime start,
                                                                                    BookingStatus bookingStatus);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime end,
                                                           BookingStatus status);
}