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
        film.setId(generateId());
        films.put(film.getId(), film);

        log.debug("Film {} added.", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
        return films.get(filmId);
    }

    @Override
    public boolean filmExists(Integer filmId) {
        return films.containsKey(filmId);
    }


    protected void clear() {
        id = 0;
        films.clear();
    }

    private int generateId() {
        return ++id;
    }
}
