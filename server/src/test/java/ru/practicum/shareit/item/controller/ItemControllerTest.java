package ru.practicum.shareit.item.controller;

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

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private InputItemDto inputItemDto;
    private ItemDto itemDto;
    private final String userId = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        inputItemDto = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .id(inputItemDto.getId())
                .name(inputItemDto.getName())
                .description(inputItemDto.getDescription())
                .available(inputItemDto.getAvailable())
                .build();
    }

    @SneakyThrows
    @Test
    void shouldCreateItem() {
        Mockito.when(itemService.create(any(InputItemDto.class), anyLong())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header(userId, 1)
                        .content(objectMapper.writeValueAsString(inputItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldUpdateItem() {
        InputItemDto updatedinputItemDto = InputItemDto.builder()
                .id(1L)
                .name("new itemname")
                .description("description")
                .available(true)
                .build();
        itemDto.setName("new itemname");
        Mockito.when(itemService.update(anyLong(), any(InputItemDto.class), anyLong())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header(userId, 1)
                        .content(objectMapper.writeValueAsString(updatedinputItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetById() {
        Mockito.when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header(userId, 1)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetItemsByOwner() {
        List<ItemDto> items = List.of(itemDto);
        Mockito.when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(items);
        mockMvc.perform(get("/items")
                        .header(userId, 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(items))
                );
    }

    @SneakyThrows
    @Test
    void shouldFindItem() {
        List<ItemDto> items = List.of(itemDto);
        Mockito.when(itemService.findItem(anyString(), anyInt(), anyInt())).thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "1")
                        .param("text", "name")
                )
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(items))
                );
    }

    @SneakyThrows
    @Test
    void shouldPostComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .build();
        when(itemService.postComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header(userId, 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(commentDto)));
    }

}