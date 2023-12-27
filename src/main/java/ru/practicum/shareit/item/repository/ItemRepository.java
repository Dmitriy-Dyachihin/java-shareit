package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item item);

    Item getById(Long id);

    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> findItem(String text);

    Collection<Item> getAll();
}
