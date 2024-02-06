package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {



    public Booking toBooking(BookingInputDto bookingInputDto) {
        return Booking.builder()
                .id(bookingInputDto.getId())
                .start(bookingInputDto.getStart())
                .end(bookingInputDto.getEnd())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static BookingShortDto toBookingShortDto(BookingDto bookingDto) {
        return BookingShortDto.builder()
                .id(bookingDto.getId())
                .itemId(bookingDto.getItem().getId())
                .bookerId(bookingDto.getBooker().getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

}
