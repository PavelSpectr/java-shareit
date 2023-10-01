package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для BookingServiceImpl")
class BookingServiceImplTest {
    private BookingService bookingService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    @DisplayName("Получение бронирований пользователя по id и фильтру")
    public void shouldReturnBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyLong(),
                Mockito.any())).thenReturn(bookings);

        List<Booking> resultBookings = bookingService.getBookingsByBookerId(booker.getId(),
                "ALL", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех текущих бронирований пользователя по id")
    public void shouldReturnCurrentBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByBookerId(booker.getId(), "CURRENT", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех прошедших бронирований пользователя по id")
    public void shouldReturnPastBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByBookerId(booker.getId(), "PAST", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }


    @Test
    @DisplayName("Получение всех будущих бронирований пользователя по id")
    public void shouldReturnFutureBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByBookerId(booker.getId(), "FUTURE", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех ожидающих бронирований пользователя по id")
    public void shouldReturnWaitingBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(BookingStatus.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByBookerId(booker.getId(), "WAITING", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех отклоненных бронирований пользователя по id")
    public void shouldReturnRejectedBookingsByBookerId() {
        User booker = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.setBooker(booker))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(BookingStatus.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByBookerId(booker.getId(), "REJECTED", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Бросить исключение при получении бронирований с неверным статусом фильтра")
    public void shouldThrowExceptionWhenGetBookingsByInvalidStatusFilter() {
        User booker = generator.nextObject(User.class);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingsByBookerId(
                booker.getId(), "UNSUPPORTED_STATUS", Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("Бросить исключение при запрашивании бронирований несуществующего пользователя")
    public void shouldThrowExceptionWhenGetBookingsByNonExistentBookerId() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByBookerId(1L, "ALL", Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("Получение бронирований владельца вещи по ID и статусу")
    public void shouldReturnBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(Mockito.anyLong(),
                Mockito.any())).thenReturn(bookings);

        List<Booking> resultBookings = bookingService.getBookingsByItemOwnerId(owner.getId(), "ALL",
                Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Бросить исключение при запрашивании бронирований для несуществующего владельца вещи")
    public void shouldThrowExceptionWhenGetBookingsByNonExistentItemOwnerId() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.getBookingsByItemOwnerId(1L, "ALL",
                        Pageable.ofSize(10)));
    }

    @Test
    @DisplayName("Получение всех текущих бронирований для владельца вещи по id")
    public void shouldReturnCurrentBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByItemOwnerId(owner.getId(), "CURRENT", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех прошедших бронирований для владельца вещи по id")
    public void shouldReturnPastBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByItemOwnerId(owner.getId(), "PAST", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех будущих бронирований для владельца вещи по id")
    public void shouldReturnFutureBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByItemOwnerId(owner.getId(), "FUTURE", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех ожидающих бронирований для владельца вещи по id")
    public void shouldReturnWaitingBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(BookingStatus.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByItemOwnerId(owner.getId(), "WAITING", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение всех отклоненных бронирований для владельца вещи по id")
    public void shouldReturnRejectedBookingsByItemOwnerId() {
        User owner = generator.nextObject(User.class);
        List<Booking> bookings = generator.objects(Booking.class, 3)
                .peek(booking -> booking.getItem().setOwner(owner))
                .collect(Collectors.toList());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository
                        .findAllByItem_OwnerIdAndStatusOrderByStartDesc(
                                Mockito.anyLong(),
                                Mockito.any(BookingStatus.class),
                                Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> resultBookings = bookingService
                .getBookingsByItemOwnerId(owner.getId(), "REJECTED", Pageable.ofSize(10));

        Assertions.assertEquals(bookings.size(), resultBookings.size());
        Assertions.assertEquals(bookings, resultBookings);
    }

    @Test
    @DisplayName("Получение бронирования по id")
    public void shouldReturnBookingById() {
        Booking booking = generator.nextObject(Booking.class);

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Booking resultBooking = bookingService.getBookingById(booking.getId(), booking.getBooker().getId());

        Assertions.assertEquals(booking, resultBooking);
    }

    @Test
    @DisplayName("Бросить исключение при запрашивании несуществующего бронирования")
    public void shouldThrowExceptionWhenGetNonExistentBookingById() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(1L, 1L));
    }

    @Test
    @DisplayName("Создание бронирования")
    public void shouldCreateBooking() {
        Booking booking = generator.nextObject(Booking.class);
        User booker = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        Booking resultBooking = bookingService.createBooking(booking, booker.getId());

        Assertions.assertEquals(booking, resultBooking);
    }

    @Test
    @DisplayName("Бросить исключение при создании бронирования несуществующим пользователем")
    public void shouldThrowExceptionWhenCreateBookingByNonExistentBookerId() {
        Booking booking = generator.nextObject(Booking.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(booking, 1L));
    }

    @Test
    @DisplayName("Бросить исключение при создании бронирования для несуществующей вещи")
    public void shouldThrowExceptionWhenCreateBookingForNonExistentItem() {
        Booking booking = generator.nextObject(Booking.class);
        User booker = generator.nextObject(User.class);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(booking, 1L));
    }

    @Test
    @DisplayName("Бросить исключение при создании бронирования для недоступной вещи")
    public void shouldThrowExceptionWhenCreateBookingForUnavailableItem() {
        User booker = generator.nextObject(User.class);
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        item.setAvailable(false);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(ValidationException.class, () ->
                bookingService.createBooking(booking, 1L));
    }

    @Test
    @DisplayName("Бросить исключение при создании бронирования для вещи," +
            "у которой владелец и пользователь совпадают")
    public void shouldThrowExceptionWhenCreateBookingForItemWithSameOwnerAndBooker() {
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        item.setOwner(user);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(booking, user.getId()));
    }

    @Test
    @DisplayName("Одобрение или отклонение бронирования")
    public void shouldApproveOrRejectBooking() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        Booking approvedBooking = bookingService.approveOrRejectBooking(booking.getId(), true,
                booking.getItem().getOwner().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        booking.setStatus(BookingStatus.WAITING);

        Booking rejectedBooking = bookingService.approveOrRejectBooking(booking.getId(), false,
                booking.getItem().getOwner().getId());
        Assertions.assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
    }

    @Test
    @DisplayName("Бросить исключение при подтверждении" +
            "или отклонении бронирования пользователем, не имеющим прав на это")
    public void shouldThrowExceptionWhenApproveOrRejectBookingByItemOwnerNotEqualUser() {
        Long userId = 2L;
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().getOwner().setId(1L);

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class, () ->
                bookingService.approveOrRejectBooking(booking.getId(), true, userId));
    }

    @Test
    @DisplayName("Бросить исключение при одобрении или отклонении бронирования, когда статус не WAITING")
    public void shouldThrowExceptionWhenApproveOrRejectBookingWithInvalidStatus() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class, () ->
                bookingService.approveOrRejectBooking(booking.getId(), true,
                        booking.getItem().getOwner().getId()));
    }
}