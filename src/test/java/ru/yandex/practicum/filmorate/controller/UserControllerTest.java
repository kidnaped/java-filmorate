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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    String url = "/users";
    String validUser = "{\"login\":\"dolore\"," +
            "\"name\":\"Nick Name\"" +
            ",\"email\":\"mail@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"}";
    String validUser2 = "{\"login\":\"dolore2\"," +
            "\"name\":\"Nick Name2\"" +
            ",\"email\":\"mail2@mail.ru\"," +
            "\"birthday\":\"1946-08-22\"}";
    String userNoName = "{\"login\":\"common\"," +
            "\"email\":\"friend@common.ru\"," +
            "\"birthday\":\"2000-08-20\"}";
    String userFailLogin = "{\"login\":\"dolore ullamco\"," +
            "\"email\":\"yandex@mail.ru\"," +
            "\"birthday\":\"1946-08-20\"}";
    String userFailBirthday = "{\"login\":\"dolore\"," +
            "\"name\":\"\"," +
            "\"email\":\"test@mail.ru\"," +
            "\"birthday\":\"2446-08-20\"}";
    String userFailEmail = "{\"login\":\"dolore\"," +
            "\"name\":\"\"," +
            "\"email\":\"mail.ru\"," +
            "\"birthday\":\"1980-08-20\"}";
    String validUserUpdated = "{\"login\":\"doloreUpdate\"," +
            "\"name\":\"est adipisicing\"," +
            "\"id\":1," +
            "\"email\":\"mail@yandex.ru\"," +
            "\"birthday\":\"1976-09-20\"}";
    String userUnknownForUpdate = "{\"login\":\"doloreUpdate\"," +
            "\"name\":\"est adipisicing\"," +
            "\"id\":9999," +
            "\"email\":\"mail@yandex.ru\"," +
            "\"birthday\":\"1976-09-20\"}";


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    public void tearDown() {
    }

    // POST tests
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndCorrectJsonWhenPostValidUser() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("dolore"))
                .andExpect(jsonPath("$.name").value("Nick Name"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("1946-08-20"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode200AndNameEqualsLoginWhenPostUserWithNoName() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userNoName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("common"))
                .andExpect(jsonPath("$.name").value("common"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("friend@common.ru"))
                .andExpect(jsonPath("$.birthday").value("2000-08-20"));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostUserWithFailLogin() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFailLogin))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must match \"^\\S+$\"")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostUserWithFailBirthday() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFailBirthday))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must be a date in the past or in the present")));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCode400WhenPostUserWithFailEmail() {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFailEmail))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("must be a well-formed email address")));
    }


    // PUT tests
    // valid user
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndUpdatedUserWhenPutValidUser() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUser));

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserUpdated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("doloreUpdate"))
                .andExpect(jsonPath("$.name").value("est adipisicing"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(jsonPath("$.birthday").value("1976-09-20"));
    }

    // unknown user
    @Test
    @SneakyThrows
    public void shouldReturnCode404AndExceptionWhenUpdateInvalidUser() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUser));

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUnknownForUpdate))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        .getMessage()
                        .contains("User is not found.")));
    }

    //GET tests
    @Test
    @SneakyThrows
    public void shouldReturnCode200AndUsersWhenGetUsers() {
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUser));

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUser2));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("dolore"))
                .andExpect(jsonPath("$[0].email").value("mail@mail.ru"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].login").value("dolore2"))
                .andExpect(jsonPath("$[1].email").value("mail2@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}