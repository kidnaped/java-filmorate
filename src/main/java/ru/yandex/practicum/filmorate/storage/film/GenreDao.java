package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    Genre addGenre(Genre genre);
    List<Genre> getGenres();
    Genre getById(Integer genreId);
    List<Genre> getByFilmId(Integer filmId);
}
