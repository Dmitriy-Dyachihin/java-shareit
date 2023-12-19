package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        validateId(id);
        return users.get(id);
    }

    @Override
    public User create(User user) {
        validateEmail(user);
        assignId();
        user.setId(id);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        validateId(user.getId());
        validateEmail(user);
        User updatedUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            updatedUser.setEmail(user.getEmail());
        }
        users.put(updatedUser.getId(), updatedUser);
        return users.get(user.getId());
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        users.remove(id);
    }

    private void validateId(Long id) {
        if (id != 0 && !users.containsKey(id)) {
            throw new NotFoundException("Пользователь не существует");
        }
    }

    private void validateEmail(User user) {
        if (users.values()
                .stream()
                .anyMatch(userStored -> (userStored.getEmail().equalsIgnoreCase(user.getEmail())
                        && !userStored.getId().equals(user.getId())))) {
            throw new ConflictException("Уже существует пользователя с такой почтой");
        }

    }

    private Long assignId() {
        return ++id;
    }
}
