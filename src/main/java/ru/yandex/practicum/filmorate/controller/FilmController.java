package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

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
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable
                            @NotNull @Positive Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> addLikeFromUser(
            @PathVariable @NotNull @Positive Integer filmId,
            @PathVariable @NotNull @Positive Integer userId
    ){
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> removeLikeFromUser(
            @PathVariable @NotNull @Positive Integer filmId,
            @PathVariable @NotNull @Positive Integer userId
    ){
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular?count={size}")
    public List<Film> findMostLikedFilms(@RequestParam(required = false, defaultValue = "10") @Positive Integer count,
                                         @PathVariable @NotNull @Positive Integer size
    ){
        return filmService.getMostLikedFilms(count, size);
    }
}
