package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private static long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(assignId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getById(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findItem(String text) {
        return items.values()
                .stream()
                .filter(i -> ((i.getName().toLowerCase().contains(text.toLowerCase()) && i.getAvailable() ||
                        (i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()))))
                .collect(Collectors.toList());
    }

    public Collection<Item> getAll() {
        return items.values();
    }

    private Long assignId() {
        return ++id;
    }

}
