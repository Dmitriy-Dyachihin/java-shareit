package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody InputItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.debug("Создание вещи, id владельца = {}", ownerId);
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody InputItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId) {
        log.debug("Создание вещи c id={}, id владельца = {}", id, ownerId);
        return itemClient.update(id, itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("Получение вещи c id={} пользователем с id={}", id, userId);
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Min(0) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получение списка вещей владельца с id={}", ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItem(@RequestParam(name = "text") String text,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Min(0) @RequestParam(defaultValue = "10") Integer size,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        log.debug("Поиск вещей с запросом {}", text);
        return itemClient.findItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(USER_ID_HEADER) Long authorId, @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.debug("Публткация комментария пользователем с id={} к вещи с id={}", authorId, itemId);
        return itemClient.postComment(authorId, itemId, commentDto);
    }

}
