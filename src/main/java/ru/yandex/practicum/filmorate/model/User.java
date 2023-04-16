package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    @PositiveOrZero
    private int id;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("EMAIL", email);
        map.put("LOGIN", login);
        map.put("USER_NAME", name);
        map.put("BIRTHDAY", birthday);
        return map;
    }
}
