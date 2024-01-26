package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jsonComment;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @SneakyThrows
    @Test
    void testCommentDto() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.of(2024, 1, 2, 0, 0, 0))
                .build();
        JsonContent<CommentDto> result = jsonComment.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().format(formatter));
        CommentDto commentDto2 = jsonComment.parseObject(result.getJson());
        assertThat(commentDto).isEqualTo(commentDto2);
    }

}