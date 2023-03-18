package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (filmStorage.filmExists(film.getId())) {
            log.warn("Failed to register film.");
            throw new FailedRegistrationException("Film is already registered.");
        }
        validateReleaseDate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.filmExists(film.getId())) {
            log.warn("Failed to update film data: {}", film.getName());
            throw new NotFoundException("Film is not found.");
        }
        validateReleaseDate(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(@NotNull @Positive Integer filmId) {
       return getFilmOrThrow(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Map<String, Integer> addLike(
            @NotNull @Positive Integer filmId,
            @NotNull @Positive Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        film.addLike(user.getId());
        filmStorage.updateFilm(film);

        return Map.of(
                "Film", film.getId(),
                "Liked by User", user.getId()
        );
    }

    public Map<String, Integer> removeLike(
            @NotNull @Positive Integer filmId,
            @NotNull @Positive Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        film.removeLike(user.getId());
        filmStorage.updateFilm(film);

        return Map.of(
                "Film", film.getId(),
                "Like removed by User", user.getId()
        );
    }

    public List<Film> getMostLikedFilms(@NotNull @Positive Integer count) {
        List<Film> allFilms = filmStorage.getFilms();

        return allFilms.stream()
                .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(Integer filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
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
}
