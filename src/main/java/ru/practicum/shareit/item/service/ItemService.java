package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(InputItemDto itemDto, Long ownerId);

    ItemDto update(Long id, InputItemDto itemDto, Long ownerId);

    ItemDto getById(Long id, Long userId);

    Collection<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size);

    Collection<ItemDto> findItem(String text, Integer from, Integer size);

    CommentDto postComment(Long authorId, Long itemId, CommentDto commentDto);

}
