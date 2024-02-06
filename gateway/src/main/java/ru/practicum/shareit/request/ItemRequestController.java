package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.InputItemRequestDto;

import javax.validation.constraints.Min;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {


    private final ItemRequestClient itemRequestClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Validated @RequestBody InputItemRequestDto inputItemRequestDto,
                                                @RequestHeader(userIdHeader) Long requesterId) {
        log.debug("Создание запроса пользователем с id={}", requesterId);
        return itemRequestClient.createRequest(inputItemRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequester(@RequestHeader(userIdHeader) Long requesterId) {
        log.debug("Получение списка запросов пользователя c id={}", requesterId);
        return itemRequestClient.getRequestsByRequester(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(userIdHeader) Long requesterId,
                                         @PathVariable Long requestId) {
        log.debug("Получение запроса пользователем с id={}, id запроса ={}", requesterId, requestId);
        return itemRequestClient.getRequestById(requesterId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(userIdHeader) Long requesterId,
                                               @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                               @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получение запросов всех пользователей кроме пользователя с id={}", requesterId);
        return itemRequestClient.getAllRequests(requesterId, from, size);
    }
}