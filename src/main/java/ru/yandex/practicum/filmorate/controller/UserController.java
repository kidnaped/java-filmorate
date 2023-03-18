package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable(required = false) Integer userId) {
       return userService.findById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriendsByUserId(@PathVariable Integer userId) {
        return userService.findFriendsByUserId(userId);
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
        return userService.makeFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Map<String, Integer> removeFriend(
            @PathVariable Integer userId,
            @PathVariable Integer friendId
    ){
        return userService.stopBeingFriends(userId, friendId);
    }
}
