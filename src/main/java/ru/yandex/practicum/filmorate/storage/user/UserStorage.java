package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User create(User user);
    User update(User user);
    List<User> findAll();
    User findById(Integer userId);

    List<User> findFriendsByUserId(Integer userId);

    Map<String, Integer> addToFriendList(Integer userId, Integer friendId);

    Map<String, Integer> removeFromFriendList(Integer userId, Integer friendId);

    List<Integer> getAllUsersId();

    List<User> findCommonFriendsById(Integer userId, Integer otherId);
}
