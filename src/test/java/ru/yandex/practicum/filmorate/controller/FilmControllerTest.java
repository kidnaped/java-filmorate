package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class FilmControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController filmController;
    @Autowired
    private WebApplicationContext webApplicationContext;

    String url = "/films";
    String validFilm = "{\"name\":\"nisi eiusmod\"," +
            "\"description\":\"adipisicing\"," +
            "\"releaseDate\":\"1967-03-25\"," +
            "\"duration\":100}";
    String validFilm2 = "{\"name\":\"nisi eiusmod2\"," +
            "\"description\":\"adipisicing2\"," +
            "\"releaseDate\":\"1967-03-27\"," +
            "\"duration\":120}";
    String filmFailName = "{\"name\":\"\"," +
            "\"description\":\"Description\"," +
            "\"releaseDate\":\"1900-03-25\"," +
            "\"duration\":200}";
    String filmFailDescription = "{\"name\":\"Film name\"," +
            "\"description\":\"Пятеро друзей ( комик-группа «Шарло»), " +
            "приезжают в город Бризуль. Здесь они хотят разыскать " +
            "господина Огюста Куглова, который задолжал им деньги, " +
            "а именно 20 миллионов. о Куглов, который за время " +
            "«своего отсутствия», стал кандидатом Коломбани.\"," +
            "\"releaseDate\":\"1900-03-25\"," +
            "\"duration\":200}";
    String filmFailReleaseDate = "{\"name\":\"Name\"," +
            "\"description\":\"Description\"," +
            "\"releaseDate\":\"1890-03-25\"," +
            "\"duration\":200}";
    String filmFailDuration = "{\"name\":\"Name\"," +
            "\"description\":\"Descrition\"," +
            "\"releaseDate\":\"1980-03-25\"," +
            "\"duration\":-200}";
    String validFilmUpdate = "{\"id\": 1," +
            "\"name\":\"Film Updated\"," +
            "\"releaseDate\":\"1989-04-17\"," +
            "\"description\":\"New film update decription\"," +
            "\"duration\":190," +
            "\"rate\":4}";
    String filmUnknownUpdate = "{\"id\":9999," +
            "\"name\":\"Film Updated\"," +
            "\"releaseDate\":\"1989-04-17\"," +
            "\"description\":\"New film update decription\"," +
            "\"duration\":190," +
            "\"rate\":4}";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    public void tearDown() {
        filmController.clear();
    }

    // POST tests
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndCorrectJsonWhenPostValidFilm() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validFilm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(jsonPath("$.description").value("adipisicing"))
                .andExpect(jsonPath("$.duration").value(100))
                .andExpect(jsonPath("$.releaseDate").value("1967-03-25"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostFilmWithFailName() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmFailName))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must not be blank")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostFilmWithFailDescription() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmFailDescription))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must match \"^.{1,200}$\"")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostFilmWithFailReleaseDate() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmFailReleaseDate))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("Film release date must be after 28/12/1985")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostFilmWithFailDuration() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmFailDuration))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must be greater than 0")));
    }

    // PUT tests
    // valid film
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndUpdatedFilmWhenPutValidData() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validFilm));

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validFilmUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film Updated"))
                .andExpect(jsonPath("$.description").value("New film update decription"))
                .andExpect(jsonPath("$.duration").value(190))
                .andExpect(jsonPath("$.releaseDate").value("1989-04-17"));
    }

    // unknown film
    @Test
    @SneakyThrows
    public void shouldReturnCode404AndExceptionWhenUpdateInvalidFilm() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validFilm));

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmUnknownUpdate))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("Film is not found.")));
    }

    // GET tests
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndFilmWhenRequestGetFilms() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validFilm));

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validFilm2));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("nisi eiusmod"))
                .andExpect(jsonPath("$[0].description").value("adipisicing"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("nisi eiusmod2"))
                .andExpect(jsonPath("$[1].description").value("adipisicing2"));
    }

}