package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTest {
    private final UserDbStorage userStorage;
    private final UserService userService;
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
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

        user2 = new User();
        user2.setEmail("user2@email.com");
        user2.setLogin("user2");
        user2.setName("user2Name");
        user2.setBirthday(LocalDate.of(2000, 3, 4));

        film1 = new Film();
        film1.setName("film1Name");
        film1.setDescription("film1Descr");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setMpa(new Mpa(1));

        film2 = new Film();
        film2.setName("film2Name");
        film2.setDescription("film2Descr");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film2.setMpa(new Mpa(2));
    }

    // User tests
    @Test
    public void shouldRegisterNewUser() {
        Optional<User> optUser = Optional.ofNullable(userStorage.create(user1));
        assertThat(optUser).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).hasFieldOrPropertyWithValue("login", "user1");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "user1Name");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1999, 3, 4));
                });
    }

    @Test
    public void shouldReturnValidUserWhenGettingUserById() {
        userStorage.create(user1);
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

    @Test
    public void shouldReturnEmptyWhenGettingNotRegisteredUser() {
        Optional<User> userOpt = Optional.ofNullable(userStorage.findById(3));
        assertThat(userOpt).isEmpty();
    }

    @Test
    public void shouldUpdateUserData() {
        userStorage.create(user1);
        user1.setName("updatedName");
        user1.setEmail("updated@email.com");
        user1.setLogin("updatedLogin");
        user1.setBirthday(LocalDate.of(1922, 3, 4));

        Optional<User> optUser = Optional.ofNullable(userStorage.update(user1));
        assertThat(optUser).isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).hasFieldOrPropertyWithValue("login", "updatedLogin");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "updatedName");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "updated@email.com");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1922, 3, 4));
                });
    }

    @Test
    public void shouldReturnListOfRegisteredUsers() {
        userStorage.create(user1);
        userStorage.create(user2);

        List<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void shouldAddAndDeleteFriendForRegisteredUsers() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriend(user1.getId(), user2.getId());
        Optional<User> optUser = Optional.ofNullable(userStorage.findById(user1.getId()));
        assertThat(optUser).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>(Set.of(2))));
        Optional<User> optUser2 = Optional.ofNullable(userStorage.findById(user2.getId()));
        assertThat(optUser2).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>()));
        userStorage.deleteFriend(user1.getId(), user2.getId());
        Optional<User> optUser3 = Optional.ofNullable(userStorage.findById(user1.getId()));
        assertThat(optUser3).hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("friends", new TreeSet<>()));
    }

    @Test
    public void shouldReturnCommonFriends() {
        User common = new User();
        common.setEmail("common@email.com");
        common.setLogin("common");
        common.setName("common2Name");
        common.setBirthday(LocalDate.of(2001, 3, 4));

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(common);

        userStorage.addFriend(user1.getId(), common.getId());
        userStorage.addFriend(user2.getId(), common.getId());

        List<User> commons = userService.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commons.size());
        assertEquals(3, commons.get(0).getId());
    }

    // Film tests
    @Test
    public void shouldRegisterNewFilm() {
        Optional<Film> optFilm = Optional.ofNullable(filmStorage.addFilm(film1));
        assertThat(optFilm).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "film1Name");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "film1Descr");
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 120);
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2020, 1, 1));
                });
    }

    @Test
    public void shouldReturnFilmWhenGettingById() {
        filmStorage.addFilm(film1);
        Optional<Film> optFilm = Optional.ofNullable(filmStorage.findFilmById(1));
        assertThat(optFilm).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "film1Name");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "film1Descr");
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 120);
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2020, 1, 1));
                });
    }

    @Test
    public void shouldReturnEmptyWhenGettingNotRegisteredFilm() {
        Optional<Film> optFilm = Optional.ofNullable(filmStorage.findFilmById(1));
        assertThat(optFilm).isEmpty();
    }

    @Test
    public void shouldUpdateFilmData() {
        filmStorage.addFilm(film1);
        film1.setName("film1NameUpdated");
        film1.setDescription("film1DescrUpdated");
        film1.setDuration(122);
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setMpa(new Mpa(3));

        Optional<Film> optFilm = Optional.ofNullable(filmStorage.updateFilm(film1));
        assertThat(optFilm).isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "film1NameUpdated");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "film1DescrUpdated");
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 122);
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2022, 1, 1));
                });
    }

    @Test
    public void shouldReturnListOfFilms() {
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    public void shouldAddAndDeleteLikesForFilm() {
        filmStorage.addFilm(film1);
        userStorage.create(user1);
        assertEquals(0, filmStorage.findFilmById(1).getLikes().size());

        filmStorage.addLike(film1.getId(), user1.getId());
        assertEquals(1, filmStorage.findFilmById(film1.getId()).getLikes().size());
        assertTrue(filmStorage.findFilmById(film1.getId()).getLikes().contains(user1.getId()));

        filmStorage.deleteLike(film1.getId(), user1.getId());
        assertEquals(0, filmStorage.findFilmById(film1.getId()).getLikes().size());
    }

    @Test
    public void shouldReturnMostLikedFilms() {
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.create(user1);
        userStorage.create(user2);

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());

        List<Film> films = filmService.getPopularFilms(2);
        assertEquals(2, films.size());
        assertEquals(2, films.get(0).getId());
    }

    @Test
    public void shouldReturnGenresList() {
        Optional<List<Genre>> genres = Optional.ofNullable(genreDao.getGenres());
        assertThat(genres).isPresent();
        assertEquals(6, genres.get().size());
    }

    @Test
    public void shouldReturnGenreById() {
        Optional<Genre> genre = Optional.ofNullable(genreDao.getById(1));
        assertThat(genre).isPresent();
        assertEquals("Комедия", genre.get().getName());
    }

    @Test
    public void shouldReturnMpaList() {
        Optional<List<Mpa>> mpas = Optional.ofNullable(mpaDao.getMpa());
        assertThat(mpas).isPresent();
        assertEquals(5, mpas.get().size());
    }

    @Test
    public void shouldReturnMpaById() {
        Optional<Mpa> mpa = Optional.ofNullable(mpaDao.getById(1));
        assertThat(mpa).isPresent();
        assertEquals("G", mpa.get().getName());
    }
}