package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    ItemDto getById(Long id);

    Collection<ItemDto> getItemsByOwner(Long ownerId);

    Collection<ItemDto> findItem(String text);
}
