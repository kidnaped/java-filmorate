package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    public UserStorage getUserStorage() {
        return userStorage;
    }

    // addToFriendList
    public Map<String, Integer> addToFriendList(Integer userId, Integer friendId) {
        registrationValidation(userId, friendId);
        getUserStorage().findById(userId).addFriend(friendId);
        return Map.of(
                "User", userId,
                "Friend added", friendId
        );
    }

    // removeFromFriendList
    public Map<String, Integer> removeFromFriendList(Integer userId, Integer friendId) {
        registrationValidation(userId, friendId);
        getUserStorage().findById(userId).removeFriend(friendId);
        return Map.of(
                "User", userId,
                "Friend removed", friendId
        );
    }

    // getCommonFriends
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        registrationValidation(userId, otherId);
        return userStorage.findAll().
    }

    private void registrationValidation(Integer userId, Integer otherId) {
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("UserId is not found.");
        }
        if (!userStorage.getAllUsersId().contains(otherId)) {
            throw new NotFoundException("FriendId is not found.");
        }
    }
}
