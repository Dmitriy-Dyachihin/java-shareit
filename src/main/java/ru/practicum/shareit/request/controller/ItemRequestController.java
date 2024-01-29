package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {


    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@Validated @RequestBody InputItemRequestDto inputItemRequestDto,
                                        @RequestHeader(userIdHeader) Long requesterId) {
        return itemRequestService.createRequest(inputItemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByRequester(@RequestHeader(userIdHeader) Long requesterId) {
        return itemRequestService.getRequestsByRequester(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(userIdHeader) Long requesterId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requesterId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIdHeader) Long requesterId,
                                               @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                               @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(requesterId, from, size);
    }
}