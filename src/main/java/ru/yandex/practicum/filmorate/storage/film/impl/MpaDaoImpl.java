package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaDao;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Mpa> getMpa() {
        String sqlAllGetMpas = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlAllGetMpas,
                (rs, rowNum) -> new Mpa(
                        rs.getInt("MPA_ID"),
                        rs.getString("MPA_NAME")
                )
        );
    }

    @Override
    public Mpa getById(Integer mpaId) {
        String sqlGetMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlGetMpa, mpaId);
        if (rowSet.next()) {
            return new Mpa(mpaId, rowSet.getString("MPA_NAME"));
        } else {
            throw new NotFoundException("No MPA rating with such ID");
        }
    }
}
