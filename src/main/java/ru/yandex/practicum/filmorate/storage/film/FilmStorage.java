package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> getFilms();
    Optional<Film> findFilmById(Integer filmId);
    List<Film> getMostLikedFilms(Integer count);
    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
}
