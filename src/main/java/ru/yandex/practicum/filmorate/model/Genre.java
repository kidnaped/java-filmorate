package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Genre {
    private Integer id;
    private String name;

    @JsonIgnore
    private Integer filmId;

    public Genre(int genreId, String genreName) {
        this.id = genreId;
        this.name = genreName;
    }
}
