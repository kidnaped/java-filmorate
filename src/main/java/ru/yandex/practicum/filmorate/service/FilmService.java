package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDao mpaDao;

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        setMpaForFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId());
        validateReleaseDate(film);
        setMpaForFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
       return getFilmOrThrow(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public String addLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        filmStorage.addLike(film.getId(), user.getId());

        return String.format("Film: %s, liked by user: %s.", film.getName(), user.getName());
    }

    public String removeLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        filmStorage.deleteLike(film.getId(), user.getId());

        return String.format("Remove like by user: %s, from film: %s.", user.getName(), film.getName());
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = new ArrayList<>(filmStorage.getFilms());
        films.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        Collections.reverse(films);

        if (count > films.size()) {
            count = films.size();
        }
        return films.subList(0, count);
    }

    private Film getFilmOrThrow(int filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            log.warn("Failed to validate film.");
            throw new NotFoundException("Film with given ID is not found.");
        }
        return film;
    }

    private User getUserOrThrow(int userId) {
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

    private void setMpaForFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaDao.getById(film.getMpa().getId()));
        }
    }
}
