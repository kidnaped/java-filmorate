package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        String sqlGetGenres = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlGetGenres,
                (rs, rowNum) -> new Genre(
                        rs.getInt("GENRE_ID"),
                        rs.getString("GENRE_NAME")
                )
        );
    }

    @Override
    public Genre getById(Integer genreId) {
        String sqlGetGenre = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetGenre, genreId);
        if (rowSet.next()) {
            return new Genre(genreId, rowSet.getString("GENRE_NAME"));
        } else {
            throw new NotFoundException("No genre with such ID.");
        }
    }

}
