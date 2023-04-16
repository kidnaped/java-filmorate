package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("APP_USER")
                .usingGeneratedKeyColumns("USER_ID");
        user.setId(jdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlUpdate = "UPDATE APP_USER SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlUpdate,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sqlGetAll = "SELECT * FROM APP_USER";
        return jdbcTemplate.query(sqlGetAll, this::makeUser);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        String sqlGetById = "SELECT * FROM APP_USER WHERE USER_ID = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlGetById, this::makeUser, userId));
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlAdd = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlAdd, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlDelete = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlDelete, userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("USER_NAME"));
        user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
        addFriendsToUser(user.getId())
                .forEach(friend -> user.getFriends().add(friend.getId()));
        return user;
    }

    private List<User> addFriendsToUser(Integer userId) {
        String sqlFindFriends = "SELECT * FROM APP_USER AS u " +
                "JOIN FRIENDSHIP AS f ON u.USER_ID = f.USER_ID " +
                "WHERE f.USER_ID = ?";
        return jdbcTemplate.query(sqlFindFriends, this::makeUser, userId);
    }
}
