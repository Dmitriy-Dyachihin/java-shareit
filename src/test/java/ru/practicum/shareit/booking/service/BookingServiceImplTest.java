package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/main/resources/data.sql"})
class BookingServiceImplTest {

    private final BookingServiceImpl bookingService;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    private BookingInputDto bookingInputDto;
    private UserDto userDto;
    private UserDto userDto2;
    private InputItemDto inputItemDto;
    private InputItemDto inputItemDto2;
    private UserDto createdUser;
    private ItemDto createdItem;
    private ItemDto createdItem2;
    private BookingDto bookingDto;
    private UserDto createdUser2;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();
        createdUser = userService.create(userDto);
        userDto2 = UserDto.builder()
                .id(2L)
                .name("username2")
                .email("user2@mail.ru")
                .build();
        createdUser2 = userService.create(userDto2);
        inputItemDto = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("item description")
                .available(true)
                .build();
        createdItem = itemService.create(inputItemDto, createdUser.getId());
        inputItemDto2 = InputItemDto.builder()
                .id(2L)
                .name("itemname2")
                .description("item2 description")
                .available(true)
                .build();
        createdItem2 = itemService.create(inputItemDto2, createdUser2.getId());
        bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .itemId(createdItem.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDto = bookingService.create(createdUser2.getId(), bookingInputDto);
    }

    @Test
    void shouldCreateBooking() {
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .id(2L)
                .itemId(createdItem2.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto bookingDto2 = bookingService.create(createdUser.getId(), bookingInputDto2);
        assertEquals(bookingDto2.getId(), bookingInputDto2.getId());
        assertEquals(bookingDto2.getItem().getId(), bookingInputDto2.getItemId());
        assertEquals(bookingDto2.getStart(), bookingInputDto2.getStart());
        assertEquals(bookingDto2.getEnd(), bookingInputDto2.getEnd());
        assertEquals(bookingDto2.getBooker().getId(), createdUser.getId());

    }

    @Test
    void shouldNotCreateBookingWithUncorrectedItem() {
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .id(1L)
                .itemId(6L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertThatThrownBy(() -> bookingService.create(createdUser.getId(), bookingInputDto2)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotCreateBookingIfItemUnavailable() {
        InputItemDto inputItemDto2 = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("item description")
                .available(false)
                .build();
        ItemDto createdItem2 = itemService.create(inputItemDto2, createdUser.getId());
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .id(1L)
                .itemId(createdItem2.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        assertThatThrownBy(() -> bookingService.create(createdUser.getId(), bookingInputDto2)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldNotCreateBookingByOwner() {
        assertThatThrownBy(() -> bookingService.create(createdUser.getId(), bookingInputDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotCreateBookingWithUncorrectedTime() {
        BookingInputDto bookingInputDto2 = BookingInputDto.builder()
                .id(2L)
                .itemId(createdItem2.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        assertThatThrownBy(() -> bookingService.create(createdUser.getId(), bookingInputDto2)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldApprove() {
        BookingDto bookingDto2 = bookingService.approve(createdUser.getId(), bookingDto.getId(), true);
        assertEquals(bookingDto2.getId(), bookingInputDto.getId());
        assertEquals(bookingDto2.getItem().getId(), bookingInputDto.getItemId());
        assertEquals(bookingDto2.getStart(), bookingInputDto.getStart());
        assertEquals(bookingDto2.getEnd(), bookingInputDto.getEnd());
        assertEquals(bookingDto2.getBooker().getId(), createdUser2.getId());
        assertEquals(bookingDto2.getStatus(), Status.APPROVED);
    }

    @Test
    void shouldNotApprove() {
        BookingDto bookingDto2 = bookingService.approve(createdUser.getId(), bookingDto.getId(), false);
        assertEquals(bookingDto2.getId(), bookingInputDto.getId());
        assertEquals(bookingDto2.getItem().getId(), bookingInputDto.getItemId());
        assertEquals(bookingDto2.getStart(), bookingInputDto.getStart());
        assertEquals(bookingDto2.getEnd(), bookingInputDto.getEnd());
        assertEquals(bookingDto2.getBooker().getId(), createdUser2.getId());
        assertEquals(bookingDto2.getStatus(), Status.REJECTED);
    }

    @Test
    void shouldNotApproveWithUncorrectedBookingId() {
        assertThatThrownBy(() -> bookingService.approve(createdUser.getId(), 2L, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotApproveTwice() {
        bookingService.approve(createdUser.getId(), bookingDto.getId(), true);
        assertThatThrownBy(() -> bookingService.approve(createdUser.getId(), bookingDto.getId(), true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldGetById() {
        BookingDto bookingDto2 = bookingService.getById(createdUser.getId(), bookingDto.getId());
        assertEquals(bookingDto2.getId(), bookingInputDto.getId());
        assertEquals(bookingDto2.getItem().getId(), bookingInputDto.getItemId());
        assertEquals(bookingDto2.getStart(), bookingInputDto.getStart());
        assertEquals(bookingDto2.getEnd(), bookingInputDto.getEnd());
        assertEquals(bookingDto2.getBooker().getId(), createdUser2.getId());
    }

    @Test
    void shouldNotGetByUncorrectedBookingId() {
        assertThatThrownBy(() -> bookingService.getById(createdUser.getId(), 3L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotGetByUncorrectedUserId() {
        UserDto userDto3 = UserDto.builder()
                .id(3L)
                .name("username3")
                .email("user3@mail.ru")
                .build();
        UserDto createdUser3 = userService.create(userDto3);
        assertThatThrownBy(() -> bookingService.getById(createdUser3.getId(), bookingDto.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetByBookerAll() {
        Collection<BookingDto> bookings = bookingService.getByUser("ALL", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of(bookingDto));
    }

    @Test
    void shouldGetByBookerCurrent() {
        Collection<BookingDto> bookings = bookingService.getByUser("CURRENT", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByBookerPast() {
        Collection<BookingDto> bookings = bookingService.getByUser("PAST", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByBookerFuture() {
        Collection<BookingDto> bookings = bookingService.getByUser("FUTURE", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of(bookingDto));
    }

    @Test
    void shouldGetByBookerWaiting() {
        Collection<BookingDto> bookings = bookingService.getByUser("WAITING", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of(bookingDto));
    }

    @Test
    void shouldGetByBookerRejected() {
        Collection<BookingDto> bookings = bookingService.getByUser("REJECTED", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByBookerUnsupportedState() {
        assertThatThrownBy(() -> bookingService.getByUser("SOMETHING", createdUser2.getId(), 0, 1)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldGetByOwner() {
        Collection<BookingDto> bookings = bookingService.getByOwner("ALL", createdUser.getId(), 0, 1);
        assertEquals(bookings, List.of(bookingDto));
    }

    @Test
    void shouldGetByOwnerCurrent() {
        Collection<BookingDto> bookings = bookingService.getByOwner("CURRENT", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByOwnerPast() {
        Collection<BookingDto> bookings = bookingService.getByOwner("PAST", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByOwnerFuture() {
        Collection<BookingDto> bookings = bookingService.getByOwner("FUTURE", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByOwnerWaiting() {
        Collection<BookingDto> bookings = bookingService.getByOwner("WAITING", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByOwnerRejected() {
        Collection<BookingDto> bookings = bookingService.getByOwner("REJECTED", createdUser2.getId(), 0, 1);
        assertEquals(bookings, List.of());
    }

    @Test
    void shouldGetByOwnerUnsupportedState() {
        assertThatThrownBy(() -> bookingService.getByOwner("SOMETHING", createdUser2.getId(), 0, 1)).isInstanceOf(BadRequestException.class);
    }

}