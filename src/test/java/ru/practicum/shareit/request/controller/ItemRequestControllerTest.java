package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private InputItemRequestDto inputItemRequestDto;
    private ItemRequestDto itemRequestDto;
    private final String userId = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        inputItemRequestDto = InputItemRequestDto.builder()
                .description("item request description")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(inputItemRequestDto.getDescription())
                .created(inputItemRequestDto.getCreated())
                .build();
    }

    @SneakyThrows
    @Test
    void shouldCreateItemRequest() {
        Mockito.when(itemRequestService.createRequest(any(InputItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .header(userId, 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetRequestsByRequester() {
        List<ItemRequestDto> requests = List.of(itemRequestDto);
        Mockito.when(itemRequestService.getRequestsByRequester(anyLong())).thenReturn(requests);
        mockMvc.perform(get("/requests")
                        .header(userId, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(requests))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetRequestById() {
        Mockito.when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header(userId, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestDto))
                );
    }

    @SneakyThrows
    @Test
    void shouldGetAll() {
        List<ItemRequestDto> requests = List.of(itemRequestDto);
        Mockito.when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(requests);
        mockMvc.perform(get("/requests/all")
                        .header(userId, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(requests))
                );
    }

}