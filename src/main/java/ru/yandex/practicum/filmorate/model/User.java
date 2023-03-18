package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class User {
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
    @NonNull
    private Set<Integer> friends;

    public void addFriend(Integer id) {
        friends.add(id);
    }
    public void removeFriend(Integer id) {
        friends.remove(id);
    }

    public List<Integer> getFriends() {
        return new ArrayList<>(friends);
    }
}
