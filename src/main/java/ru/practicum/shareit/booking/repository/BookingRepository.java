package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start,
                                                                                 LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstBookingByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(Long itemId,
                                                                                         LocalDateTime start,
                                                                                         BookingStatus status);

    Optional<Booking> findFirstBookingByItemIdAndStartAfterAndStatusNotOrderByStart(Long itemId, LocalDateTime start,
                                                                                    BookingStatus bookingStatus);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime end,
                                                           BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND ((b.start <= :end) AND (b.end >= :start))")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

    //List<Booking> findAllByBookingByItemOrderByStartDateAsc(Booking booking);
}