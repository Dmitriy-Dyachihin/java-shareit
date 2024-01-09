package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {

    @NotNull
    private Long id;
    private Long itemId;
    private Long bookerId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
