package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
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
    public Film getById(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> addLikeFromUser(
            @PathVariable Integer filmId,
            @PathVariable Integer userId
    ){
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Map<String, Integer> removeLikeFromUser(
            @PathVariable Integer filmId,
            @PathVariable Integer userId
    ){
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findMostLikedFilms(@RequestParam(required = false, defaultValue = "10") Integer count){
        return filmService.getMostLikedFilms(count);
    }
}

