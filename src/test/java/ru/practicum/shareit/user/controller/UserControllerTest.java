package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;

    private UserDto userDtoCreated;
    private UserDto userDtoUpdated;

    @BeforeEach
    public void setUp() {
        userDtoCreated = new UserDto(1L, "username", "user@mail.ru");
        userDtoUpdated = new UserDto(1L, "username", "newuser@mail.ru");
    }

    @SneakyThrows
    @Test
    void shouldCreateUser() {
        Mockito.when(userService.create(any(UserDto.class))).thenReturn(userDtoCreated);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoCreated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoCreated)));

    }

    @SneakyThrows
    @Test
    void shouldUpdate() {
        Mockito.when(userService.update(anyLong(), any(UserDto.class))).thenReturn(userDtoUpdated);
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoUpdated))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoUpdated)));
    }

    @SneakyThrows
    @Test
    void shouldGetById() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(userDtoUpdated);
        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoUpdated)));
    }

    @SneakyThrows
    @Test
    void shouldDelete() {
        mockMvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(1L);
    }

    @SneakyThrows
    @Test
    void shouldGetAll() {
        Mockito.when(userService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

}