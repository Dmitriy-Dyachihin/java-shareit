package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingDto create(Long bookerId, BookingInputDto bookingInputDto) {
        Booking booking = bookingMapper.toBooking(bookingInputDto);
        User booker = userMapper.toUser(userService.getUserById(bookerId));
        Item item = itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Не существует товара с указанным id"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Товар не доступен для бронирования");
        } else if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Нельзя бронировать собственные вещи");
        } else if (booking.getStart() == null || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd() == null || booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Некорректное время");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        Booking bookingToSave = bookingRepository.save(booking);
        return toBookingDto(bookingToSave);
    }

    @Transactional
    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        User booker = userMapper.toUser(userService.getUserById(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Не существует бронирования с указанным id"));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Статус уже установлен");
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем товара");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        User user = userMapper.toUser(userService.getUserById(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Не существует бронирования с указанным id"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является ни владельцем товара, ни его арендатором");
        }
        return toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getByUser(String state, Long userId, Integer from, Integer size) {
        User user = userMapper.toUser(userService.getUserById(userId));
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker(user, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerAndStatus(user, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerAndStatus(user, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingDto> getByOwner(String state, Long userId, Integer from, Integer size) {
        User owner = userMapper.toUser(userService.getUserById(userId));
        List<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwner(owner, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(owner, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(owner, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(owner, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.REJECTED, pageable);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
