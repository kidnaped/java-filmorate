package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @PositiveOrZero
    private int id;
    @NotNull
    @NotEmpty
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @NotEmpty
    @Pattern(regexp = "^\\S+$")
    private String login;
    private String name;
    @PastOrPresent
    LocalDate birthday;
}
