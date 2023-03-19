package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FailedRegistrationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (userStorage.userExists(user.getId())) {
            log.warn("Failed to add new user: {}", user.getName());
            throw new FailedRegistrationException("User is already registered.");
        }
        setDefaultNameIfEmpty(user);

        return userStorage.create(user);
    }

    public User updateUser(User user) {
        if (!userStorage.userExists(user.getId())) {
            log.warn("Failed to update user's data: {}", user.getName());
            throw new NotFoundException("User is not found.");
        }
        setDefaultNameIfEmpty(user);
        return userStorage.update(user);
    }

    public User findById(@NotNull @Positive Integer userId) {
        return getUserOrThrow(userId);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public List<User> findFriendsByUserId(@NotNull @Positive Integer userId) {
        User user = getUserOrThrow(userId);
        List<Integer> friendsId = new ArrayList<>(user.getFriends());
        List<User> userFriends = new ArrayList<>();

        friendsId.forEach(id -> userFriends.add(userStorage.findById(id)));

        return userFriends;
    }

    // addToFriendList
    public Map<String, Integer> makeFriends(
            @Valid @NotNull @Positive Integer userId,
            @Valid @NotNull @Positive Integer friendId
    ) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.addFriend(friendId);
        userStorage.update(user);

        friend.addFriend(userId);
        userStorage.update(friend);

        return Map.of(
                "User", userId,
                "Friend added", friendId
        );
    }

    // removeFromFriendList
    public Map<String, Integer> stopBeingFriends(
            @NotNull @Positive Integer userId,
            @NotNull @Positive Integer friendId
    ) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.removeFriend(friend.getId());
        userStorage.update(user);

        friend.removeFriend(user.getId());
        userStorage.update(friend);

        return Map.of(
                "User", userId,
                "Friend deleted", friendId
        );
    }

    // getCommonFriends
    public List<User> getCommonFriends(
            @NotNull @Positive Integer userId,
            @NotNull @Positive Integer otherId
    ) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        userFriends.retainAll(otherUserFriends);

        return userFriends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Integer userId) {
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

    protected void clear() {
        userStorage.clear();
    }
}

