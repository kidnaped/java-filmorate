package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @NotNull
    @PositiveOrZero
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;

    public User(Integer id,
                String email,
                String login,
                String name,
                LocalDate birthday
    ) {
        this.friends = new HashSet<>();
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name == null ? login : name;
        this.birthday = birthday;
    }

    public void addFriend(Integer id) {
        friends.add(id);
    }
    public void removeFriend(Integer id) {
        friends.remove(id);
    }
    public Set<Integer> getFriends() {
        return friends;
    }
}
