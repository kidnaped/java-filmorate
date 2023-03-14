package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final SortedMap<Integer, User> users = new TreeMap<>();
    private int id = 0;

    @Override
    public User addUser(User user) {
        try {
            if (users.containsValue(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already registered.");
            }
            manageEmptyUserName(user);

            user.setId(generateId());
            users.put(user.getId(), user);

            log.debug("New user {} added.", user.getName());
            return user;
        } catch (ResponseStatusException e) {
            log.warn("Failed to add new user: " + e.getMessage());
            throw new ResponseStatusException(e.getStatus(), e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            if (users.containsKey(user.getId())) {
                manageEmptyUserName(user);
                users.put(user.getId(), user);

                log.debug("Users {} data updated.", user.getName());
                return user;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found.");
            }
        } catch (ResponseStatusException e) {
            log.warn("Failed to update user's data: {}", e.getMessage());
            throw new ResponseStatusException(e.getStatus(), e.getMessage());
        }
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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
}
