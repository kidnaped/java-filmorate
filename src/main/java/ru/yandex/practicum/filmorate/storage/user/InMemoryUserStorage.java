package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final SortedMap<Integer, User> users = new TreeMap<>();
    private int id = 0;

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);

        log.debug("New user {} with ID {} added.", user.getName(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);

        log.debug("Users {} data updated.", user.getName());
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer userId) {
        return users.get(userId);
    }

    @Override
    public boolean userExists(Integer id) {
        return users.containsKey(id);
    }

    protected void clear() {
        id = 0;
        users.clear();
    }

    private int generateId() {
        return ++id;
    }


}
