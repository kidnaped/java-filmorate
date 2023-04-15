package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

@Component
public class UserDbStorage implements UserStorage {
    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findById(Integer userId) {
        return null;
    }

    @Override
    public Set<User> findUserFriends(Integer userId) {
        return null;
    }

    @Override
    public String addFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public String deleteFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public Set<User> findCommonFriends(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public void clear() {

    }
}
