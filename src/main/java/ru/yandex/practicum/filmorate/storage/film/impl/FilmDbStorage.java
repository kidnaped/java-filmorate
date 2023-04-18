package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("FILM_ID");

        int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(filmId);

        filmGenreTableUpdate(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm = "UPDATE FILM SET FILM_ID = ?, FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
                "DURATION = ?, MPA = ? WHERE FILM_ID = ?";
        String sqlDeleteGenre = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlUpdateFilm,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update(sqlDeleteGenre, film.getId());

        filmGenreTableUpdate(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlGetAllFilms = "SELECT * FROM FILM AS f " +
                "LEFT JOIN FILM_GENRE AS fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetAllFilms);
        Map<Integer, Film> films = new HashMap<>();
        List<Genre> genres = new ArrayList<>();

        while (rowSet.next()) {
            Film film = new Film();
            film.setId(rowSet.getInt("FILM_ID"));
            film.setName(rowSet.getString("FILM_NAME"));
            film.setDescription(rowSet.getString("DESCRIPTION"));
            if (rowSet.getDate("RELEASE_DATE") != null) {
                film.setReleaseDate(rowSet.getDate("RELEASE_DATE").toLocalDate());
            }
            film.setDuration(rowSet.getInt("DURATION"));
            if (rowSet.getInt("MPA") != 0) {
                film.setMpa(makeMpa(rowSet.getInt("MPA")));
            }

            films.put(film.getId(), film);

            if (rowSet.getString("GENRE_NAME") != null) {
               Genre genre = new Genre();
               genre.setFilmId(rowSet.getInt("FILM_ID"));
               genre.setId(rowSet.getInt("GENRE_ID"));
               genre.setName(rowSet.getString("GENRE_NAME"));
               genres.add(genre);
            }
        }

        for (Genre genre : genres) {
            films.get(genre.getFilmId()).getGenres().add(genre);
        }

        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(int filmId) {
        String sqlFindFilm = "SELECT * FROM FILM WHERE FILM_ID = ?";
        if (isFilmExists(filmId, sqlFindFilm)) {
            return jdbcTemplate.queryForObject(sqlFindFilm, this::makeFilm, filmId);
        } else {
            return null;
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlAddLike = "INSERT INTO FILM_LIKED (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddLike, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlAddLike = "DELETE FROM FILM_LIKED WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlAddLike, filmId, userId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        String sqlGetGenres = "SELECT fg.GENRE_ID, g.GENRE_NAME " +
                "FROM FILM_GENRE AS fg " +
                "JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ? " +
                "ORDER BY fg.GENRE_ID";
        String sqlGetLikes = "SELECT USER_ID FROM FILM_LIKED WHERE FILM_ID = ?";

        film.setId(rs.getInt("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        if (rs.getInt("MPA") != 0) {
            film.setMpa(makeMpa(rs.getInt("MPA")));
        }

        List<Genre> genres = jdbcTemplate.query(sqlGetGenres, (resultSet, row) ->
                new Genre(resultSet.getInt("GENRE_ID"),
                        resultSet.getString("GENRE_NAME")),
                film.getId());

        if (!genres.isEmpty()) {
            film.setGenres(new HashSet<>(genres));
        }

        List<Integer> likes = jdbcTemplate.query(sqlGetLikes,
                (resSet, row) -> resSet.getInt("USER_ID"),
                film.getId());

        film.getLikes().addAll(likes);

        return film;
    }

    private Mpa makeMpa(Integer mpaId) {
        String sqlGetMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetMpa, mpaId);
        if (rowSet.next()) {
            return new Mpa(mpaId, rowSet.getString("MPA_NAME"));
        } else {
            throw new NotFoundException("No MPA rating with such ID");
        }
    }

    private boolean isFilmExists(int filmId, String sql) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        return rowSet.next();
    }

    private void filmGenreTableUpdate(Film film) {
        List<Genre> genres = new ArrayList<>(film.getGenres());
        if (!genres.isEmpty()) {
            String sqlAddGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sqlAddGenre,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }
}
