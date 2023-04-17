package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);
    User update(User user);
    List<User> findAll();
    User findById(int userId);
    void addFriend(int userId, int friendId);
    void deleteFriend(int userId, int friendId);
}
