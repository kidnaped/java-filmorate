package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
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
    private Integer duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Integer> likes = new HashSet<>();

    public Film(int id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Mpa mpa
    ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Mpa mpa
    ){
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
