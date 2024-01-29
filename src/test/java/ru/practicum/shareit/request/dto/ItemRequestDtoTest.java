package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private JacksonTester<InputItemRequestDto> jsonInput;

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item request description")
                .requesterId(1L)
                .created(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .items(List.of())
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(itemRequestDto.getRequesterId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(formatter));
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(itemRequestDto.getItems());
        ItemRequestDto itemRequestDto2 = json.parseObject(result.getJson());
        assertThat(itemRequestDto).isEqualTo(itemRequestDto2);
    }

    @SneakyThrows
    @Test
    void testInputItemRequestDto() {
        InputItemRequestDto inputItemRequestDto = InputItemRequestDto.builder()
                .description("item request description")
                .build();
        JsonContent<InputItemRequestDto> result = jsonInput.write(inputItemRequestDto);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(inputItemRequestDto.getDescription());
        InputItemRequestDto inputItemRequestDto2 = jsonInput.parseObject(result.getJson());
        assertThat(inputItemRequestDto).isEqualTo(inputItemRequestDto2);
    }

}