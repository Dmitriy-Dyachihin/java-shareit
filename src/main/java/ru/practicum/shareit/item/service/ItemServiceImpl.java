package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
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
        Item item = itemRepository.save(newItem);
        log.info("Добавлена новая вещь с id={}", item.getId());
        return itemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item updatedItem = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с указанным id не существует"));
        if (updatedItem.getOwner().getId() != ownerId) {
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
        if (item.getOwner().getId() == userId) {
            Booking lastBooking = bookingRepository.findFirstByItemAndStatusNotAndStartLessThan(item,
                    Status.REJECTED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            if (lastBooking != null) {
                itemDto.setLastBooking(bookingMapper.toBookingShortDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }
            Booking nextBooking = bookingRepository.findFirstByItemAndStatusNotAndStartAfter(item,
                    Status.REJECTED, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));
            if (nextBooking != null) {
                itemDto.setNextBooking(bookingMapper.toBookingShortDto(nextBooking));
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
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует"));
        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(user);
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), Sort.by(Sort.Direction.DESC, "created"));
        log.info("Получен список вещей пользователя с id={}", ownerId);
        itemDtos.forEach(itemDto -> {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusNotAndStartLessThan(itemDto.getId(),
                    Status.REJECTED, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
            if (lastBooking != null) {
                itemDto.setLastBooking(bookingMapper.toBookingShortDto(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusNotAndStartAfter(itemDto.getId(),
                    Status.REJECTED, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));
            if (nextBooking != null) {
                itemDto.setNextBooking(bookingMapper.toBookingShortDto(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }
            setComments(itemDto, comments);
        });
        return itemDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> findItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Получен результат поиска подстроки {}", text);
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAndAvailable(text, text, true)
                .stream()
                .map(ItemMapper::toItemDto)
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
        Comment comment = commentMapper.toComment(commentDto);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(authorId,
                itemId, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException("Пользователь с указаннмы id не брал в аренду указанную вещь");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void validateOwnerId(Long ownerId) {
        if (!userRepository.findAll().contains(userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id не существует")))) {
            throw new NotFoundException("Данный пользователь не выставлял товар");
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
        if (!itemRepository.findAll().contains(itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с указанным id не существует")))) {
            throw new NotFoundException("Нет товара с указанным id");
        }
    }

    private void validateOwner(Long ownerId, Item item) {
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем указанного товара");
        }
    }

    private void setBookings(ItemDto itemDto, List<BookingShortDto> bookings) {
        itemDto.setLastBooking(bookings.stream()
                .filter(bookingShortDto -> bookingShortDto.getItemId() == itemDto.getId() &&
                        bookingShortDto.getEnd().isBefore(LocalDateTime.now()))
                .reduce((a,b) -> a).orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(bookingShortDto -> bookingShortDto.getItemId() == itemDto.getId() &&
                        bookingShortDto.getStart().isAfter(LocalDateTime.now()))
                .findFirst().orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId() == itemDto.getId())
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList())
        );
    }
}
