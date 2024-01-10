package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestBody BookingInputDto bookingInputDto, @RequestHeader(userIdHeader) Long userId) {
        return bookingService.create(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(userIdHeader) Long userId, @PathVariable(name = "bookingId") Long bookingId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(userIdHeader) Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getByUser(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(userIdHeader) Long userId) {
        return bookingService.getByUser(state, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(userIdHeader) Long userId) {
        return bookingService.getByOwner(state, userId);
    }
}
