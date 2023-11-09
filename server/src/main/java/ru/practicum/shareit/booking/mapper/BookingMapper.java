package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.booking.dto.BookingDetailsInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class BookingMapper {
    public BookingDetailsInfoDto toBookingDetailsDto(Booking booking) {
        var itemDto = new BookingDetailsInfoDto.ItemDto(booking.getItem().getId(), booking.getItem().getName());
        var bookerDto = new BookingDetailsInfoDto.BookerDto(booking.getBooker().getId(), booking.getBooker().getName());

        return BookingDetailsInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(bookerDto)
                .status(booking.getStatus())
                .build();
    }

    public List<BookingDetailsInfoDto> toBookingDetailsDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDetailsDto)
                .collect(toList());
    }

    public BookingDatesDto toBookingDatesDto(Booking booking) {
        return BookingDatesDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public Booking toBooking(BookingCreationDto bookingCreationDto) {
        Item item = Item.builder().id(bookingCreationDto.getItemId()).build();

        return Booking.builder()
                .id(bookingCreationDto.getId())
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .item(item)
                .build();
    }
}