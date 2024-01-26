package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private JacksonTester<InputItemDto> jsonInput;

    @SneakyThrows
    @Test
    void testItemDto() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
        ItemDto itemDto2 = json.parseObject(result.getJson());
        assertThat(itemDto).isEqualTo(itemDto2);
    }

    @SneakyThrows
    @Test
    void testInputItemDto() {
        InputItemDto inputItemDto = InputItemDto.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .available(true)
                .build();
        JsonContent<InputItemDto> result = jsonInput.write(inputItemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(inputItemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(inputItemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(inputItemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(inputItemDto.getAvailable());
        InputItemDto inputItemDto2 = jsonInput.parseObject(result.getJson());
        assertThat(inputItemDto).isEqualTo(inputItemDto2);
    }

}