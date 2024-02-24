package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/main/resources/schema.sql"})
class UserServiceImplTest {

    private final UserServiceImpl userService;

    private final UserDto user = new UserDto(1L, "username", "user@mail.ru");
    private UserDto createdUser;

    @BeforeEach
    void setUp() {
        createdUser = userService.create(user);
    }

    @Test
    void shouldCreateUser() {
        UserDto userNew = new UserDto(2L, "username2", "user2@mail.ru");
        UserDto createdUserNew = userService.create(userNew);
        assertEquals(createdUserNew.getId(), userNew.getId());
        assertEquals(createdUserNew.getName(), userNew.getName());
        assertEquals(createdUserNew.getEmail(), userNew.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        user.setEmail("newuser@mail.ru");
        UserDto updatedUser = userService.update(user.getId(), user);
        assertEquals(updatedUser.getId(), user.getId());
        assertEquals(updatedUser.getName(), user.getName());
        assertEquals(updatedUser.getEmail(), user.getEmail());
    }

    @Test
    void shouldNotUpdateByUncorrectedId() {
        user.setName("new name");
        assertThatThrownBy(() -> userService.update(2L, user)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetById() {
        UserDto findingUser = userService.getUserById(user.getId());
        assertEquals(findingUser.getId(), user.getId());
        assertEquals(findingUser.getName(), user.getName());
        assertEquals(findingUser.getEmail(), user.getEmail());
    }

    @Test
    void shouldNotGetByUncorrectedId() {
        assertThatThrownBy(() -> userService.getUserById(2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetAll() {
        assertEquals(userService.getAll(), List.of(createdUser));
    }

    @Test
    void shouldDelete() {
        userService.delete(createdUser.getId());
        assertEquals(userService.getAll(), Collections.emptyList());
    }

}