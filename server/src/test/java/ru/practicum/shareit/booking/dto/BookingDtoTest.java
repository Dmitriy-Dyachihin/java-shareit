package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    private JacksonTester<BookingInputDto> jsonInput;

    @Autowired
    private JacksonTester<BookingShortDto> jsonShort;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @SneakyThrows
    @Test
    void testBookingDto() {
        Item item = Item.builder()
                .id(1L)
                .name("itemname")
                .description("description")
                .build();
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("user@mail.ru")
                .build();
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .status(Status.WAITING)
                .build();
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
        BookingDto bookingDto2 = json.parseObject(result.getJson());
        assertThat(bookingDto).isEqualTo(bookingDto2);
    }

    @SneakyThrows
    @Test
    void testBookingInputDto() {
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .build();
        JsonContent<BookingInputDto> result = jsonInput.write(bookingInputDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingInputDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingInputDto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingInputDto.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingInputDto.getEnd().format(formatter));
        BookingInputDto bookingInputDto2 = jsonInput.parseObject(result.getJson());
        assertThat(bookingInputDto).isEqualTo(bookingInputDto2);

    }

    @SneakyThrows
    @Test
    void testBookingShortDto() {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .itemId(1L)
                .bookerId(1L)
                .start(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .end(LocalDateTime.of(2025, 1, 2, 0, 0, 0))
                .build();
        JsonContent<BookingShortDto> result = jsonShort.write(bookingShortDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingShortDto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingShortDto.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(bookingShortDto.getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingShortDto.getStart().format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingShortDto.getEnd().format(formatter));
        BookingShortDto bookingShortDto2 = jsonShort.parseObject(result.getJson());
        assertThat(bookingShortDto).isEqualTo(bookingShortDto2);

    }
}