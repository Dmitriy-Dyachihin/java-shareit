package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadParameterException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        validateOwnerId(ownerId);
        Item newItem = itemMapper.toItem(itemDto);
        validateItem(newItem);
        User owner = userRepository.getUserById(ownerId);
        newItem.setOwner(owner);
        Item item = itemRepository.create(newItem);
        log.info("Добавлена новая вещь с id={}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        validateOwnerId(ownerId);
        validateItemId(id);
        Item updatedItem = itemRepository.getById(id);
        validateOwner(ownerId, updatedItem);
        if (itemMapper.toItem(itemDto).getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemMapper.toItem(itemDto).getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemMapper.toItem(itemDto).getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        log.info("Обновлена вещь с id={}", updatedItem.getId());
        return itemMapper.toItemDto(itemRepository.update(updatedItem));
    }

    @Override
    public ItemDto getById(Long id) {
        validateItemId(id);
        log.info("Получена вещь с id={}", id);
        return itemMapper.toItemDto(itemRepository.getById(id));
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        validateOwnerId(ownerId);
        log.info("Получен список вещей пользователя с id={}", ownerId);
        return itemRepository.getItemsByOwner(ownerId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Получен результат поиска подстроки {}", text);
        return itemRepository.findItem(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateOwnerId(Long ownerId) {
        if (!userRepository.getAll().contains(userRepository.getUserById(ownerId))) {
            throw new BadParameterException("Данный пользователь не выставлял товар");
        }
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("Описание не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new BadRequestException("Статус не может быть пустым");
        }
    }

    private void validateItemId(Long id) {
        if (!itemRepository.getAll().contains(itemRepository.getById(id))) {
            throw new NotFoundException("Нет товара с указанным id");
        }
    }

    private void validateOwner(Long ownerId, Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем указанного товара");
        }
    }
}
