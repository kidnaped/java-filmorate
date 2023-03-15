package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.getUserStorage().update(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUserStorage().findAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Integer userId) {
       return userService.getUserStorage().findById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriendsByUserId(@PathVariable Integer userId) {
        return userService.getUserStorage().findFriendsByUserId(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriendsBetweenUsers(
            @PathVariable Integer userId,
            @PathVariable Integer otherId
    ){
        return userService.getCommonFriends(userId, otherId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Map<String, Integer> addFriend(
            @PathVariable Integer userId,
            @PathVariable Integer friendId
    ){
        return userService.addToFriendList(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Map<String, Integer> removeFriend(
            @PathVariable Integer userId,
            @PathVariable Integer friendId
    ){
        return userService.removeFromFriendList(userId, friendId);
    }
}
