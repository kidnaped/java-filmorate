package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId());
        validateReleaseDate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer filmId) {
       return getFilmOrThrow(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public String addLike(Integer filmId, Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        filmStorage.addLike(film.getId(), user.getId());

        return String.format("Film %s: %d likes", film.getName(), film.getLikes().size());
    }

    public String removeLike(Integer filmId, Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        filmStorage.deleteLike(film.getId(), user.getId());

        return String.format("Film %s: %d likes", film.getName(), film.getLikes().size());
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getMostLikedFilms(count);
    }

    private Film getFilmOrThrow(Integer filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            log.warn("Failed to validate film.");
            throw new NotFoundException("Film with given ID is not found.");
        }
        return film;
    }

    private User getUserOrThrow(Integer userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException("User with given ID is not found.");
        }
        return user;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Failed to validate release date.");
            throw new ValidationException("Film release date must be after 28/12/1985.");
        }
    }

    public void clear() {
        filmStorage.clear();
        userStorage.clear();
    }
}
