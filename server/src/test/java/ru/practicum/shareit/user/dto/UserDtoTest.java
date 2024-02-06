package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void testJsonDto() {
        UserDto user = new UserDto(1L, "username", "user@mail.ru");
        JsonContent<UserDto> result = json.write(user);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(user.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(user.getEmail());
        UserDto userDto = json.parseObject(result.getJson());
        assertThat(userDto).isEqualTo(user);

    }

}