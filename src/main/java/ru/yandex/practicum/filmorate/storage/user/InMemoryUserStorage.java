package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FailedRegistrationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final SortedMap<Integer, User> users = new TreeMap<>();
    private int id = 0;

    @Override
    public User create(User user) {
        if (users.containsValue(user)) {
            log.warn("Failed to add new user: {}", user.getName());
            throw new FailedRegistrationException("User is already registered.");
        }
        manageEmptyUserName(user);

        user.setId(generateId());
        users.put(user.getId(), user);

        log.debug("New user {} added.", user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            manageEmptyUserName(user);
            users.put(user.getId(), user);

            log.debug("Users {} data updated.", user.getName());
            return user;
        } else {
            log.warn("Failed to update user's data: {}", user.getName());
            throw new NotFoundException("User is not found.");
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer userId) {
        userRegistrationValidation(userId);
        return users.get(userId);
    }

    @Override
    public List<User> findFriendsByUserId(Integer userId) {
        userRegistrationValidation(userId);
        List<Integer> friendsId = new ArrayList<>(users.get(userId).getFriends());
        List<User> userFriends = new ArrayList<>();

        friendsId.forEach(id -> userFriends.add(users.get(id)));

        return userFriends;
    }

    protected void clear() {
        id = 0;
        users.clear();
    }

    private int generateId() {
        return ++id;
    }

    private void manageEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void userRegistrationValidation(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("ID can not be negative or null");
        }
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with given ID is not found.");
        }
    }
}
