package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

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
    public String addFriend(
            @PathVariable Integer userId,
            @PathVariable Integer friendId
    ){
        return userService.makeFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public String removeFriend(
            @PathVariable Integer userId,
            @PathVariable Integer friendId
    ){
        return userService.stopBeingFriends(userId, friendId);
    }

    protected void clear() {
        userService.clear();
    }
}
