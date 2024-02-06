package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@Component
public class ItemMapper {

    public Item toItem(InputItemDto inputItemDto) {
        return Item.builder()
                .id(inputItemDto.getId())
                .name(inputItemDto.getName())
                .description(inputItemDto.getDescription())
                .available(inputItemDto.getAvailable())
                .build();
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(new ArrayList<>())
                .requestId((item.getItemRequest() == null ? null : item.getItemRequest().getId()))
                .build();

    }
}
