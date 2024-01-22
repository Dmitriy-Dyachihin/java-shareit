package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputItemRequestDto {

    private Long id;
    @NotNull
    @NotBlank
    @Size(max = 512)
    private String description;
    private Long requesterId;
    private LocalDateTime created;
}