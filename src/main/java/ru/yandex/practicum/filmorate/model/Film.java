package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
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
    private Integer duration;
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
    private Set<Integer> likes = new TreeSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("FILM_NAME", name);
        map.put("DESCRIPTION", description);
        map.put("RELEASE_DATE", releaseDate);
        map.put("DURATION", duration);
        if (mpa != null) {
            map.put("MPA", mpa.getId());
        }
        return map;
    }
}
