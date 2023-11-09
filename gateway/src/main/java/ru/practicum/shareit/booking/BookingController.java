package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.handlers.HeaderConstants;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsByBookerId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size
    ) {
        log.debug("+ getBookingsByBookerId: bookerId={}, state={}, ", bookerId, state);

        BookingState statusFilter = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        ResponseEntity<Object> bookings = bookingClient.getBookingsByBookerId(bookerId, statusFilter, from, size);

        log.debug("- getBookingsByBookerId: {}", bookings);

        return bookings;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemOwnerId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long itemOwnerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(100) Integer size
    ) {
        log.debug("+ getBookingsByItemOwnerId: itemOwnerId={}, state={}, ", itemOwnerId, state);

        BookingState statusFilter = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        ResponseEntity<Object> bookings = bookingClient.getBookingsByItemOwnerId(itemOwnerId, statusFilter, from, size);

        log.debug("- getBookingsByItemOwnerId: {}", bookings);

        return bookings;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        log.debug("+ getBookingById: bookingId={}", bookingId);
        ResponseEntity<Object> booking = bookingClient.getBookingById(bookingId, userId);
        log.debug("- getBookingById: {}", booking);
        return booking;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingCreationDto booking,
                                                @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        log.debug("+ createBooking: booking={}, userId={}", booking, userId);
        ResponseEntity<Object> createdBooking = bookingClient.createBooking(booking, userId);
        log.debug("- createBooking: {}", createdBooking);

        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@PathVariable Long bookingId,
                                                         @RequestParam(name = "approved") boolean isApproved,
                                                         @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        log.debug("+ approveOrRejectBooking: bookingId={}, isApproved={}, userId={}", bookingId, isApproved, userId);
        ResponseEntity<Object> updatedBooking = bookingClient.approveOrRejectBooking(bookingId, isApproved, userId);
        log.debug("- approveOrRejectBooking: {}", updatedBooking);
        return updatedBooking;
    }
}
