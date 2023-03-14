package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilmStorage().getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Integer filmId) {

    }

    @PutMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> addLikeFromUser(
            @PathVariable Integer filmId,
            @PathVariable Integer userId
    ){
        return Map.of(
                "User", userId,
                "Liked film", filmId
        );
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> removeLikeFromUser(
            @PathVariable Integer filmId,
            @PathVariable Integer userId
    ){
        return Map.of(
                "User", userId,
                "Removed like from film", filmId
        );
    }

    @GetMapping("/popular?count={size}")
    public List<Film> findMostLikedFilms(@RequestParam(required = false, defaultValue = "10") Integer count,
                                         @PathVariable Integer size
    ){

    }
}
