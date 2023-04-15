package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User create(User user);
    User update(User user);
    List<User> findAll();
    User findById(Integer userId);
    Set<User> findUserFriends(Integer userId);
    String addFriend(Integer userId, Integer friendId);
    String deleteFriend(Integer userId, Integer friendId);
    Set<User> findCommonFriends(Integer userId, Integer friendId);
    void clear();
}
