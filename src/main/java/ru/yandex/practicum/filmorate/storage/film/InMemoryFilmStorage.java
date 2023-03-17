package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.FailedRegistrationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final SortedMap<Integer, Film> films = new TreeMap<>();
    int id = 0;

    @Override
    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            log.warn("Failed to register film.");
            throw new FailedRegistrationException("Film is already registered.");
        }
        validateReleaseDate(film);

        film.setId(generateId());
        films.put(film.getId(), film);

        log.debug("Film {} added.", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getValidFilmOrThrow(film.getId());
        validateReleaseDate(film);
        films.put(film.getId(), film);

        log.debug("Film {} data updated.", film.getName());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(Integer filmId) {
        getValidFilmOrThrow(filmId);
        return films.get(filmId);
    }


    protected void clear() {
        id = 0;
        films.clear();
    }

    private int generateId() {
        return ++id;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Failed to validate release date.");
            throw new ValidationException("Film release date must be after 28/12/1985.");
        }
    }

    private Film getValidFilmOrThrow(Integer id) {
        if (id == null || id <= 0) {
            log.warn("Film ID is null or negative.");
            throw new ValidationException("ID can not be negative or null");
        }
        Film film = films.get(id);
        if (film == null) {
            log.warn("Film is not registered.");
            throw new NotFoundException("Film with given ID is not found");
        }
        return film;
    }
}
