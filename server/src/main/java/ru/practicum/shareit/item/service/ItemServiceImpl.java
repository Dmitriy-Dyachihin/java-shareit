package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingShortDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto create(InputItemDto itemDto, Long ownerId) {
        Item newItem = itemMapper.toItem(itemDto);
        if (newItem.getName() == null || newItem.getName().isBlank()) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (newItem.getDescription() == null || newItem.getDescription().isBlank()) {
            throw new BadRequestException("Описание не может быть пустым");
        }
        if (newItem.getAvailable() == null) {
            throw new BadRequestException("Статус не может быть пустым");
        }
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует"));
        newItem.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Не существует запроса с указанным id"));
            newItem.setItemRequest(itemRequest);
        }
        Item item = itemRepository.save(newItem);
        log.info("Добавлена новая вещь с id={}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long id, InputItemDto itemDto, Long ownerId) {
        Item updatedItem = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с указанным id не существует"));
        if (!updatedItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем указанного товара");
        }
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
        return itemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(Long id, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует"));
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с указанным id не существует"));
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstByItemAndStatusEqualsAndStartLessThan(item,
                    Status.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            if (lastBooking != null) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }
            Booking nextBooking = bookingRepository.findFirstByItemAndStatusEqualsAndStartAfter(item,
                    Status.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));
            if (nextBooking != null) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }
        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.DESC, "created"));
        List<CommentDto> commentDtos = comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        itemDto.setComments(commentDtos);
        log.info("Получена вещь с id={}", id);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует"));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findAllByOwnerOrderById(user, pageable);
        List<ItemDto> itemDtos = items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), Sort.by(Sort.Direction.DESC, "created"));
        log.info("Получен список вещей пользователя с id={}", ownerId);
        itemDtos.forEach(itemDto -> {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusEqualsAndStartLessThan(itemDto.getId(),
                    Status.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            if (lastBooking != null) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusEqualsAndStartAfter(itemDto.getId(),
                    Status.APPROVED, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));
            if (nextBooking != null) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }
            setComments(itemDto, comments);
        });
        return itemDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> findItem(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Получен результат поиска подстроки {}", text);
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(text, text,
                        true, pageable)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public CommentDto postComment(Long authorId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }
        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundException("Не существует пользователя с указанным id"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Не существует товара с указанным id"));
        Comment comment = toComment(commentDto);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(authorId,
                itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Пользователь с указаннмы id не брал в аренду указанную вещь");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return toCommentDto(commentRepository.save(comment));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId().equals(itemDto.getId()))
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList())
        );
    }
}
