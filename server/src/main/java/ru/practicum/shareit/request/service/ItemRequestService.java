package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(InputItemRequestDto inputItemRequestDto, Long requesterId);

    List<ItemRequestDto> getRequestsByRequester(Long requesterId);

    ItemRequestDto  getRequestById(Long requesterId, Long requestId);

    List<ItemRequestDto> getAllRequests(Long requesterId, Integer from, Integer size);
}