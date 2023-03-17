package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
       return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User findById(Integer userId) {
        return userStorage.findById(userId);
    }

    public List<User> findFriendsByUserId(Integer userId) {
        return userStorage.findFriendsByUserId(userId);
    }

    // addToFriendList
    public Map<String, Integer> addToFriendList(Integer userId, Integer friendId) {
        return userStorage.addToFriendList(userId, friendId);
    }

    // removeFromFriendList
    public Map<String, Integer> removeFromFriendList(Integer userId, Integer friendId) {
        return userStorage.removeFromFriendList(userId, friendId);
    }

    // getCommonFriends
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return userStorage.findCommonFriendsById(userId, otherId);
    }
}
