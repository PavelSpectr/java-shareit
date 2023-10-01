package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDetailsInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handlers.HeaderConstants;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDetailsInfoDto> getBookingsByBookerId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer offset,
            @RequestParam(value = "size", defaultValue = "20") Integer limit
    ) {
        log.debug("+ getBookingsByBookerId: bookerId={}, state={}, ", bookerId, state);

        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<BookingDetailsInfoDto> bookings = BookingMapper
                .toBookingDetailsDto(bookingService.getBookingsByBookerId(bookerId, state, pageable));

        log.debug("- getBookingsByBookerId: {}", bookings);

        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDetailsInfoDto> getBookingsByItemOwnerId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long itemOwnerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer offset,
            @RequestParam(value = "size", defaultValue = "20") Integer limit
    ) {
        log.debug("+ getBookingsByItemOwnerId: itemOwnerId={}, state={}, ", itemOwnerId, state);

        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<BookingDetailsInfoDto> bookings = BookingMapper
                .toBookingDetailsDto(bookingService.getBookingsByItemOwnerId(itemOwnerId, state, pageable));

        log.debug("- getBookingsByItemOwnerId: {}", bookings);

        return bookings;
    }

    @GetMapping("/{bookingId}")
    public BookingDetailsInfoDto getBookingById(@PathVariable Long bookingId,
                                                @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ getBookingById: bookingId={}", bookingId);
        BookingDetailsInfoDto booking = BookingMapper
                .toBookingDetailsDto(bookingService.getBookingById(bookingId, userId));
        log.debug("- getBookingById: {}", booking);
        return booking;
    }

    @PostMapping
    public BookingDetailsInfoDto createBooking(@RequestBody BookingCreationDto booking,
                                               @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ createBooking: booking={}, userId={}", booking, userId);

        Booking createdBooking = bookingService.createBooking(BookingMapper.toBooking(booking), userId);
        BookingDetailsInfoDto createdBookingDto = BookingMapper.toBookingDetailsDto(createdBooking);

        log.debug("- createBooking: {}", createdBookingDto);

        return createdBookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDetailsInfoDto approveOrRejectBooking(@PathVariable Long bookingId,
                                                        @RequestParam(name = "approved") boolean isApproved,
                                                        @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.debug("+ approveOrRejectBooking: bookingId={}, isApproved={}, userId={}", bookingId, isApproved, userId);

        Booking updatedBooking = bookingService.approveOrRejectBooking(bookingId, isApproved, userId);
        BookingDetailsInfoDto updatedBookingDto = BookingMapper.toBookingDetailsDto(updatedBooking);

        log.debug("- approveOrRejectBooking: {}", updatedBookingDto);

        return updatedBookingDto;
    }
}
