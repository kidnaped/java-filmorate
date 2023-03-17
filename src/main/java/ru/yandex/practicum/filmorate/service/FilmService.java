package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    //TODO
    public Map<String, Integer> addLike(Integer filmId, Integer userId) {
        return null;
    }
    //TODO
    public Map<String, Integer> removeLike(Integer filmId, Integer userId) {
        return null;
    }
    //TODO
    public List<Film> getMostLikedFilms(Integer count, Integer size) {
        return null;
    }
}
