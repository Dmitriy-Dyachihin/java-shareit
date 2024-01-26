package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/main/resources/data.sql"})
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    private InputItemRequestDto inputItemRequestDto;
    private UserDto userDto;
    private InputItemDto inputItemDto;
    private ItemRequestDto itemRequestDto;
    private UserDto createdUser;
    private ItemDto createdItem;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();
        createdUser = userService.create(userDto);
        inputItemRequestDto = InputItemRequestDto.builder()
                .description("item request description")
                .build();
        itemRequestDto = itemRequestService.createRequest(inputItemRequestDto, createdUser.getId());
        inputItemDto = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("item description")
                .available(true)
                .requestId(itemRequestDto.getId())
                .build();
        createdItem = itemService.create(inputItemDto, createdUser.getId());
    }

    @Test
    void shouldCreateRequest() {
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("username2")
                .email("user2@mail.ru")
                .build();
        UserDto createdUser2 = userService.create(userDto2);
        InputItemRequestDto inputItemRequestDto2 = InputItemRequestDto.builder()
                .description("item request description2")
                .build();
        ItemRequestDto itemRequestDto2 = itemRequestService.createRequest(inputItemRequestDto2, createdUser2.getId());
        InputItemDto inputItemDto2 = InputItemDto.builder()
                .id(2L)
                .name("itemname2")
                .description("item description2")
                .available(true)
                    .requestId(itemRequestDto2.getId())
                    .build();
        ItemDto createdItem2 = itemService.create(inputItemDto2, createdUser2.getId());
        assertEquals(itemRequestDto2.getId(), 2);
        assertEquals(itemRequestDto2.getRequesterId(), createdUser2.getId());
    }

    @Test
    void shouldGetById() {
        ItemRequestDto itemRequestDto2 = itemRequestService.getRequestById(createdUser.getId(), itemRequestDto.getId());
        assertEquals(itemRequestDto2.getId(), 1);
        assertEquals(itemRequestDto2.getRequesterId(), createdUser.getId());
        assertEquals(itemRequestDto2.getItems(), List.of(createdItem));
    }

    @Test
    void shouldNotGetByUncorrectedId() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(createdUser.getId(), 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetRequestsByRequester() {
        List<ItemRequestDto> requests = itemRequestService.getRequestsByRequester(createdUser.getId());
        assertEquals(requests.size(), 1);
    }

    @Test
    void shouldGetAllRequests() {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(createdUser.getId(), 0, 1);
        assertEquals(requests, Collections.emptyList());
    }

}