package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        getUserOrThrow(user.getId());
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserOrThrow(user.getId());
        return userStorage.update(user);
    }

    public User findById(Integer userId) {
        return getUserOrThrow(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public List<User> findFriendsByUserId(Integer userId) {
        User user = getUserOrThrow(userId);
        return new ArrayList<>(userStorage.findUserFriends(user.getId()));
    }

    // addToFriendList
    public String makeFriends(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        userStorage.addFriend(user.getId(), friend.getId());

        return String.format("%s and %s are friends.", user.getName(), friend.getName());
    }

    // removeFromFriendList
    public String stopBeingFriends(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        userStorage.deleteFriend(user.getId(), friend.getId());

        return String.format("%s and %s are not friends.", user.getName(), friend.getName());
    }

    // getCommonFriends
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        return new ArrayList<>(userStorage.findCommonFriends(user.getId(), otherUser.getId()));
    }

    private User getUserOrThrow(Integer userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("Failed to verify user. User is not registered.");
            throw new NotFoundException("User with given ID is not found.");
        }
        return user;
    }

    public void clear() {
        userStorage.clear();
    }
}

