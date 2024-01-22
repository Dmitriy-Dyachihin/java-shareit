package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@AllArgsConstructor
@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDto createRequest(InputItemRequestDto inputItemRequestDto, Long requesterId) {
        User requester = userMapper.toUser(userService.getUserById(requesterId));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(inputItemRequestDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Добавлен новый запрос вещи с id={}", itemRequest.getId());
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getRequestsByRequester(Long requesterId) {
        User requester = userMapper.toUser(userService.getUserById(requesterId));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "created"));
        log.info("Получен список запросов пользователя с id={}", requesterId);
        return addItems(requests);

    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequestById(Long requesterId, Long requestId) {
        User requester = userMapper.toUser(userService.getUserById(requesterId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Не существует запроса бронирования с указанным id"));
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        log.info("Получен запрос с id={}", requestId);
        return addItems(requests).get(0);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllRequests(Long requesterId, Integer from, Integer size) {
        User requester = userMapper.toUser(userService.getUserById(requesterId));
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNot(requesterId, pageable);
        log.info("Получен список запросов");
        return addItems(requests);
    }

    private List<ItemRequestDto> addItems(List<ItemRequest> requests) {
        List<ItemRequestDto> reqestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items =  itemRepository.findByItemRequest(request);
            List<ItemDto> itemDto = new ArrayList<>();
            for (Item item : items) {
                itemDto.add(itemMapper.toItemDto(item));
            }
            ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
            requestDto.setItems(itemDto);
            reqestDtos.add(requestDto);
        }
        return reqestDtos;
    }
}