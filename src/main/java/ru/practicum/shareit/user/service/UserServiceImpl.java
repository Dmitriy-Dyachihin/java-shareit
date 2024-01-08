package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public Collection<UserDto> getAll() {
        log.info("Получен список всех пользователей");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        log.info("Получен пользователь с id={}", id);
        return userMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует")));
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("Создан новый пользователь с id={}", user.getId());
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User updatedUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с указанным id не существует"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            updatedUser.setEmail(userDto.getEmail());
        }
        log.info("Обновлен пользователь с id={}", id);
        return userMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Удален пользователь с id={}", id);
        userRepository.deleteById(id);
    }
}
