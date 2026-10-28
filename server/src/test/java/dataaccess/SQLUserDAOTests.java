package dataaccess;

import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.deleteAll();
    }

    @Test
    public void insertUserSuccess() throws DataAccessException {
        UserData user = new UserData("morgan", "hashedPassword", "morgan@example.com");

        userDAO.insertUser(user);

        assertEquals(user, userDAO.getUser("morgan"));
    }

    @Test
    public void insertUserDuplicateUsername() throws DataAccessException {
        userDAO.insertUser(new UserData("morgan", "hashedPassword", "morgan@example.com"));

        assertThrows(DataAccessException.class,
                () -> userDAO.insertUser(new UserData("morgan", "other", "other@example.com")));
    }

    @Test
    public void insertUserMissingEmail() {
        assertThrows(DataAccessException.class,
                () -> userDAO.insertUser(new UserData("morgan", "hashedPassword", null)));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        userDAO.insertUser(new UserData("morgan", "hashedPassword", "morgan@example.com"));
        userDAO.insertUser(new UserData("sam", "otherHash", "sam@example.com"));

        UserData found = userDAO.getUser("sam");

        assertEquals("sam", found.username());
        assertEquals("otherHash", found.password());
        assertEquals("sam@example.com", found.email());
    }

    @Test
    public void getUserNotFound() throws DataAccessException {
        assertNull(userDAO.getUser("nobody"));
    }

    @Test
    public void deleteAllSuccess() throws DataAccessException {
        userDAO.insertUser(new UserData("morgan", "hashedPassword", "morgan@example.com"));
        userDAO.insertUser(new UserData("sam", "otherHash", "sam@example.com"));

        userDAO.deleteAll();

        assertNull(userDAO.getUser("morgan"));
        assertNull(userDAO.getUser("sam"));
    }
}
