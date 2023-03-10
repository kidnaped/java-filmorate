package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final SortedMap<Integer, Film> films = new TreeMap<>();
    private int id = 0;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        try {
            if (films.containsValue(film)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Film is already registered.");
            }
            validateReleaseDate(film);
            film.setId(generateId());
            films.put(film.getId(), film);
            log.debug("Film {} added.", film.getName());
            return film;
        } catch (ResponseStatusException e) {
            log.warn("Failed to add film: {}", e.getMessage());
            throw new ResponseStatusException(e.getStatus(), e.getMessage());
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            if (films.containsKey(film.getId())) {
                validateReleaseDate(film);
                films.put(film.getId(), film);
                log.debug("Film {} data updated.", film.getName());
                return film;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film is not found.");
            }
        } catch (ResponseStatusException e) {
            log.warn("Failed to update film: {}", e.getMessage());
            throw new ResponseStatusException(e.getStatus(), e.getMessage());
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    protected void clear() {
        id = 0;
        films.clear();
    }
    private int generateId() {
        return ++id;
    }

    private void validateReleaseDate(Film film) throws ResponseStatusException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film release date must be after 28/12/1985.");
        }
    }
}
