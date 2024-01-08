package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long bookerId, BookingInputDto bookingInputDto);

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getByUser(String state, Long userId);

    Collection<BookingDto> getByOwner(String state, Long userId);
}
