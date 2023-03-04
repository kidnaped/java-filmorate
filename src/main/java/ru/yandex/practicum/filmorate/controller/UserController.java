package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final SortedMap<Integer, User> users = new TreeMap<>();
    private int id = 0;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
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

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
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

    @GetMapping
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
