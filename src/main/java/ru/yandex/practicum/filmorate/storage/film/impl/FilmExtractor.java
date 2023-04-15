package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
class FilmExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Film> films = new HashSet<>();
        Set<Genre> genres = new HashSet<>();
        Set<Integer> likes = new HashSet<>();

        while (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt("FILM_ID"))
                    .name(rs.getString("FILM_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getInt("DURATION"))
                    .mpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")))
                    .build();
            int likeUserId = rs.getInt("LIKE_USER_ID");
            Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));

            if (likeUserId != 0) {
                likes.add(likeUserId);
            }

            if (genre.getId() != 0) {
                genres.add(genre);
            }

            if (films.add(film)) {
                film.setLikes(likes);
                film.setGenres(genres);
            }

            likes = new HashSet<>();
            genres = new HashSet<>();
        }

        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(films);
    }
}
