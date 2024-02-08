package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
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
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.booking.State.getState;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingInputDto bookingInputDto, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("Создание бронирования пользователем с id={}", userId);
        return bookingClient.bookItem(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable(name = "bookingId") Long bookingId,
                                          @RequestParam Boolean approved) {
        log.debug("Обновление статуса бронирования пользователем с id={}, id бронирования={}, статус {}}", userId, bookingId, approved);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        log.debug("Получение бронирования пользователем с id={}, id бронирования={}}", userId, bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader(USER_ID_HEADER) Long userId,
                                            @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получение списка бронирований пользователя с id={}", userId);
        return bookingClient.getBookings(userId, getState(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID_HEADER) Long userId,
                                             @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                             @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получение списка бронирований владельца вещей с id={}", userId);
        return bookingClient.getBookingsByOwner(userId, getState(state), from, size);
    }
}
