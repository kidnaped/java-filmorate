package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmDbStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;
    FilmExtractor filmExtractor;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmExtractor filmExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmExtractor = filmExtractor;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlAddFilm = "INSERT INTO FILM (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlAddGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sqlAddGenre, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm = "UPDATE FILM SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
                "DURATION = ?, MPA = ? WHERE FILM_ID = ?";
        String sqlDeleteGenre = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId());
        jdbcTemplate.update(sqlDeleteGenre, film.getId());

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sqlAddGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)" +
                        "SELECT ?, ? WHERE NOT EXISTS (" +
                        "SELECT 1 FROM FILM_GENRE " +
                        "WHERE FILM_ID = ? AND GENRE_ID = ?)";
                jdbcTemplate.update(sqlAddGenre, film.getId(), genre.getId(), film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlGetAllFilms = "SELECT f.*, m.MPA_NAME, fg.GENRE_ID, g.GENRE_NAME, fl.USER_ID AS LIKE_USER_ID " +
                "FROM FILM AS f " +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILM_LIKED fl ON f.FILM_ID = fl.FILM_ID " +
                "LEFT JOIN MPA m ON f.MPA = m.MPA_ID " +
                "ORDER BY f.FILM_ID";
        return jdbcTemplate.query(sqlGetAllFilms, filmExtractor);
    }

    @Override
    public Film findFilmById(Integer filmId) {
        String sqlFindFilm = "SELECT f.*, m.MPA_NAME, fg.GENRE_ID, g.GENRE_NAME, fl.USER_ID AS LIKE_USER_ID " +
                "FROM FILM AS f " +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN FILM_LIKED fl ON f.FILM_ID = fl.FILM_ID " +
                "LEFT JOIN MPA m ON f.MPA = m.MPA_ID " +
                "WHERE f.FILM_ID = ?" +
                "ORDER BY f.FILM_ID";

        return jdbcTemplate.query(sqlFindFilm, (rs, rowNum) -> makeFilm(rs), filmId).get(0);
    }

    @Override
    public List<Film> getMostLikedFilms(Integer count) {
        return null;
    }

    @Override
    public String addLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public String deleteLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public void clear() {
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")))
                .build();
    }
}
