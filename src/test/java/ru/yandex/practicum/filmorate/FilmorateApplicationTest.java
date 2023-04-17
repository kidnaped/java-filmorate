package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDaoImpl mpaDao;
    private final GenreDaoImpl genreDao;
    private User user1;
    private User user2;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setEmail("user@email.com");
        user1.setLogin("user1");
        user1.setName("user1Name");
        user1.setBirthday(LocalDate.of(1999, 3, 4));
        userStorage.create(user1);

        user2 = new User();
        user2.setEmail("user2@email.com");
        user2.setLogin("user2");
        user2.setName("user2Name");
        user2.setBirthday(LocalDate.of(2000, 3, 4));
        userStorage.create(user2);

        film1 = new Film();
        film1.setName("film1Name");
        film1.setDescription("film1Descr");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setMpa(new Mpa(1));
        filmStorage.addFilm(film1);

        film2 = new Film();
        film2.setName("film2Name");
        film2.setDescription("film2Descr");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film2.setMpa(new Mpa(2));
        filmStorage.addFilm(film2);

    }

    // User tests
    // return registered user
    @Test
    public void shouldReturnValidUserWhenGettingUserById() {
        Optional<User> userOpt = Optional.ofNullable(userStorage.findById(1));
        assertThat(userOpt).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).hasFieldOrPropertyWithValue("login", "user1");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "user1Name");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1999, 3, 4));
                });
    }

    // return Optional.empty() when for not registered user
    @Test
    public void shouldReturnEmptyWhenGettingNotRegisteredUser() {
        Optional<User> userOpt = Optional.ofNullable(userStorage.findById(3));
        assertThat(userOpt).isEmpty();
    }


}