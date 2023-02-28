package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    @PositiveOrZero
    private int id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String name;
    @NotNull
    @NotBlank
    @NotEmpty
    @Pattern(regexp="^.{1,200}$")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private double duration;
}
