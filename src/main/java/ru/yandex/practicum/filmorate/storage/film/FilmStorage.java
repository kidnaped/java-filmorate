package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film findFilmById(int filmId);

    List<Film> getMostLikedFilms(int count);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
