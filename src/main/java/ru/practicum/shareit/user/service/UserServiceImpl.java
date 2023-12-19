package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAll() {
        log.info("Получен список всех пользователей");
        return userRepository.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получен пользователь с id={}", id);
        return userMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.create(userMapper.toUser(userDto));
        log.info("Создан новый пользователь с id={}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        userDto.setId(id);
        log.info("Обновлен пользователь с id={}", id);
        return userMapper.toUserDto(userRepository.update(userMapper.toUser(userDto)));
    }

    @Override
    public void delete(Long id) {
        log.info("Удален пользователь с id={}", id);
        userRepository.delete(id);
    }
}
