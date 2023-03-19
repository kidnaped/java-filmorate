package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    @PositiveOrZero
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Pattern(regexp="^.{1,200}$")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private double duration;
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        likes.remove(userId);
    }

    public Set<Integer> getLikes() {
        return likes;
    }
}
