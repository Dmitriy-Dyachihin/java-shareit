package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;
    private BookingInputDto bookingInputDto;
    private BookingDto bookingDto;
    private InputItemDto inputItemDto;
    private UserDto userDto;
    private Item item;
    private ItemMapper itemMapper = new ItemMapper();
    private User user;
    private UserMapper userMapper = new UserMapper();
    private final String userId = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        inputItemDto = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .build();
        item = itemMapper.toItem(inputItemDto);
        bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .itemId(inputItemDto.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();
        user = userMapper.toUser(userDto);
        bookingDto = BookingDto.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(bookingInputDto.getStart())
                .end(bookingInputDto.getEnd())
                .build();

    }

    @SneakyThrows
    @Test
    void shouldCreateBooking() {
        Mockito.when(bookingService.create(anyLong(), any(BookingInputDto.class))).thenReturn(bookingDto);
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userId, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldApproveBooking() {
        bookingDto.setStatus(Status.APPROVED);
        Mockito.when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/1")
                        .header(userId, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDto))
                );
        bookingDto.setStatus(Status.WAITING);
    }

    @SneakyThrows
    @Test
    void shouldGetById() {
        Mockito.when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header(userId, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetByUser() {
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(bookingService.getByUser(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .header(userId, 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookings))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetByOwner() {
        List<BookingDto> bookings = List.of(bookingDto);
        Mockito.when(bookingService.getByOwner(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .header(userId, 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookings))
                );
    }

}