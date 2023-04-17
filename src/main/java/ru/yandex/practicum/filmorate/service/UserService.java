package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        setDefaultNameIfEmpty(user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserOrThrow(user.getId());
        setDefaultNameIfEmpty(user);
        return userStorage.update(user);
    }

    public User findById(Integer userId) {
        return getUserOrThrow(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public List<User> findFriendsByUserId(int userId) {
        User user = getUserOrThrow(userId);
        Set<Integer> friendsId = user.getFriends();
        List<User> userFriends = new ArrayList<>();
        for (Integer id : friendsId) {
            userFriends.add(getUserOrThrow(id));
        }
        return userFriends;
    }

    // addToFriendList
    public String makeFriends(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        userStorage.addFriend(user.getId(), friend.getId());

        return String.format("%s added %s in a friends list.", user.getName(), friend.getName());
    }

    // removeFromFriendList
    public String stopBeingFriends(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        userStorage.deleteFriend(user.getId(), friend.getId());

        return String.format("%s removed %s from a friends list.", user.getName(), friend.getName());
    }

    // getCommonFriends
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        List<Integer> common = new ArrayList<>(user.getFriends());
        common.retainAll(otherUser.getFriends());

        return common.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(int userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("Failed to verify user. User is not registered.");
            throw new NotFoundException("User with given ID is not found.");
        }
        return user;
    }

    private void setDefaultNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}

