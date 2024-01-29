package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/main/resources/data.sql"})
class ItemServiceImplTest {

    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestService itemRequestService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserDto ownerDto = new UserDto(1L, "username", "user@mail.ru");
    private UserDto createdOwnerDto;
    private final UserDto bookerDto = new UserDto(2L, "username2", "user2@mail.ru");
    private UserDto createdBookerDto;
    private final InputItemRequestDto inputItemRequestDto = InputItemRequestDto.builder()
            .description("description")
            .build();
    private ItemRequestDto createdItemRequestDto;
    private final InputItemDto inputItemDto = InputItemDto.builder()
            .id(1L)
            .name("itemname")
            .description("description")
            .available(true)
            .build();
    private ItemDto createdItem;
    private final BookingInputDto nextBooking = BookingInputDto.builder()
            .start(LocalDateTime.now().plusDays(3))
            .end(LocalDateTime.now().plusDays(4))
            .build();
    private BookingDto createdNextBooking;
    private final CommentDto commentDto = CommentDto.builder()
            .text("Comment")
            .build();

    @BeforeEach
    void setUp() {
        createdOwnerDto = userService.create(ownerDto);
        createdBookerDto = userService.create(bookerDto);
        createdItemRequestDto = itemRequestService.createRequest(inputItemRequestDto, createdBookerDto.getId());
        inputItemDto.setRequestId(createdItemRequestDto.getId());
        createdItem = itemService.create(inputItemDto, 1L);
        nextBooking.setItemId(inputItemDto.getId());
    }

    @Test
    void shouldCreateItem() {
        InputItemDto item2 = InputItemDto.builder()
                .id(2L)
                .name("itemname2")
                .description("description")
                .available(true)
                .build();
        ItemDto createdItem2 = itemService.create(item2, 1L);
        assertEquals(createdItem2.getId(), item2.getId());
        assertEquals(createdItem2.getName(), item2.getName());
        assertEquals(createdItem2.getDescription(), item2.getDescription());
        assertEquals(createdItem2.getAvailable(), item2.getAvailable());
    }

    @Test
    void shouldNotCreateWithEmptyName() {
        inputItemDto.setName(null);
        assertThatThrownBy(() -> itemService.create(inputItemDto, 1L)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldNotCreateWithEmptyDescription() {
        inputItemDto.setDescription(null);
        assertThatThrownBy(() -> itemService.create(inputItemDto, 1L)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldNotCreateWithUncorrectedAvailable() {
        inputItemDto.setAvailable(null);
        assertThatThrownBy(() -> itemService.create(inputItemDto, 1L)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldNotCreateWithUncorrectedOwnerId() {
        assertThatThrownBy(() -> itemService.create(inputItemDto, 3L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotCreateWithUncorrectedRequestId() {
        inputItemDto.setRequestId(3L);
        assertThatThrownBy(() -> itemService.create(inputItemDto, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateItem() {
        inputItemDto.setDescription("new description");
        ItemDto updatedItem = itemService.update(inputItemDto.getId(), inputItemDto, 1L);
        assertEquals(updatedItem.getId(), inputItemDto.getId());
        assertEquals(updatedItem.getName(), inputItemDto.getName());
        assertEquals(updatedItem.getDescription(), inputItemDto.getDescription());
        assertEquals(updatedItem.getAvailable(), inputItemDto.getAvailable());
    }

    @Test
    void shouldNotUpdateWithUncorrectedItemId() {
        assertThatThrownBy(() -> itemService.update(2L, inputItemDto, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotUpdateWithUncorrectedOwnerId() {
        assertThatThrownBy(() -> itemService.update(inputItemDto.getId(), inputItemDto, 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetByIdWithBookings() {
        createdNextBooking = bookingService.create(createdBookerDto.getId(), nextBooking);
        bookingService.approve(createdOwnerDto.getId(), createdNextBooking.getId(), true);
        ItemDto findingItem = itemService.getById(inputItemDto.getId(), 1L);
        assertEquals(findingItem.getId(), inputItemDto.getId());
        assertEquals(findingItem.getName(), inputItemDto.getName());
        assertEquals(findingItem.getDescription(), inputItemDto.getDescription());
        assertEquals(findingItem.getAvailable(), inputItemDto.getAvailable());
        assertEquals(findingItem.getNextBooking(), toBookingShortDto(createdNextBooking));
    }

    @Test
    void shouldGetByIdWithoutBookings() {
        ItemDto findingItem = itemService.getById(inputItemDto.getId(), 1L);
        assertEquals(findingItem.getId(), inputItemDto.getId());
        assertEquals(findingItem.getName(), inputItemDto.getName());
        assertEquals(findingItem.getDescription(), inputItemDto.getDescription());
        assertEquals(findingItem.getAvailable(), inputItemDto.getAvailable());
        assertNull(findingItem.getNextBooking());
    }

    @Test
    void shouldNotGetByUncorrectedUserId() {
        assertThatThrownBy(() -> itemService.getById(createdItem.getId(), 3L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotGetByUncorrectedItemId() {
        assertThatThrownBy(() -> itemService.getById(2L, createdOwnerDto.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetItemsByOwnerWithBookings() {
        createdNextBooking = bookingService.create(createdBookerDto.getId(), nextBooking);
        bookingService.approve(createdOwnerDto.getId(), createdNextBooking.getId(), true);
        List<ItemDto> items = (List<ItemDto>) itemService.getItemsByOwner(1L, 0, 1);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), inputItemDto.getId());
        assertEquals(items.get(0).getName(), inputItemDto.getName());
        assertEquals(items.get(0).getDescription(), inputItemDto.getDescription());
        assertEquals(items.get(0).getAvailable(), inputItemDto.getAvailable());
        assertEquals(items.get(0).getNextBooking(), toBookingShortDto(createdNextBooking));
    }

    @Test
    void shouldGetItemsByOwnerWithoutBookings() {
        List<ItemDto> items = (List<ItemDto>) itemService.getItemsByOwner(1L, 0, 1);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), inputItemDto.getId());
        assertEquals(items.get(0).getName(), inputItemDto.getName());
        assertEquals(items.get(0).getDescription(), inputItemDto.getDescription());
        assertEquals(items.get(0).getAvailable(), inputItemDto.getAvailable());
        assertNull(items.get(0).getNextBooking());
    }

    @Test
    void shouldNotGetItemsByUncorrectedOwner() {
        assertThatThrownBy(() -> itemService.getItemsByOwner(3L, 0, 1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindItem() {
        Collection<ItemDto> items = itemService.findItem("description", 0, 1);
        assertEquals(items.size(), 1);
    }

    @Test
    void shouldReturnEmptyListIfTextIsEmpty() {
        Collection<ItemDto> items = itemService.findItem("", 0, 1);
        assertEquals(items.size(), 0);
    }

    @Test
    void shouldPostComment() {
        User owner = User.builder()
                .id(1L)
                .name("user name")
                .email("defaultuser@mail.ru")
                .build();
        User savedOwner = userRepository.save(owner);
        User booker = User.builder()
                .id(2L)
                .name("user name2")
                .email("defaultuser2@mail.ru")
                .build();
        User savedBooker = userRepository.save(booker);
        Item item = Item.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(true)
                .owner(savedOwner)
                .build();
        Item savedItem = itemRepository.save(item);
        Booking booking = Booking.builder()
                //.id(1L)
                .item(item)
                .booker(savedBooker)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Status.APPROVED)
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        CommentDto resultComment = itemService.postComment(savedBooker.getId(), savedItem.getId(), commentDto);
        assertEquals(resultComment.getId(), 1L);
        assertEquals(resultComment.getText(), commentDto.getText());
        assertEquals(resultComment.getAuthorName(), savedBooker.getName());
    }

    @Test
    void shouldNotPostCommentWithEmptyText() {
        CommentDto wrongComment = CommentDto.builder()
                .text("")
                .build();
        assertThatThrownBy(() -> itemService.postComment(createdBookerDto.getId(), createdItem.getId(), wrongComment)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldNotPostCommentWithUncorrectedUserId() {
        assertThatThrownBy(() -> itemService.postComment(4L, createdItem.getId(), commentDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotPostCommentWithUncorrectedItemId() {
        assertThatThrownBy(() -> itemService.postComment(createdBookerDto.getId(), 2L, commentDto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotPostCommentWithUncorrectedTime() {
        assertThatThrownBy(() -> itemService.postComment(createdBookerDto.getId(), createdItem.getId(), commentDto)).isInstanceOf(BadRequestException.class);
    }

}