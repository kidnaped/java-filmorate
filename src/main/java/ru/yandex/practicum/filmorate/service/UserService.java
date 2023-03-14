package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    public void addToFriendList(Integer userId, Integer friendId) {
        getUserStorage()
                .findById(userId)
                .addFriend(friendId);
    }


    // removeFromFriendList


    // getCommonFriends

    private void idValidation(Integer id) {
        if (userStorage.findAll())
    }
}
