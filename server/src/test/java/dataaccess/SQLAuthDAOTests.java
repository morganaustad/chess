package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.deleteAll();
    }

    @Test
    public void insertAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token-1", "morgan");

        authDAO.insertAuth(auth);

        assertEquals(auth, authDAO.getAuth("token-1"));
    }

    @Test
    public void insertAuthDuplicateToken() throws DataAccessException {
        authDAO.insertAuth(new AuthData("token-1", "morgan"));

        assertThrows(DataAccessException.class,
                () -> authDAO.insertAuth(new AuthData("token-1", "sam")));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        authDAO.insertAuth(new AuthData("token-1", "morgan"));
        authDAO.insertAuth(new AuthData("token-2", "sam"));

        assertEquals("sam", authDAO.getAuth("token-2").username());
    }

    @Test
    public void getAuthNotFound() throws DataAccessException {
        assertNull(authDAO.getAuth("no-such-token"));
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        authDAO.insertAuth(new AuthData("token-1", "morgan"));
        authDAO.insertAuth(new AuthData("token-2", "sam"));

        authDAO.deleteAuth("token-1");

        assertNull(authDAO.getAuth("token-1"));
        assertNotNull(authDAO.getAuth("token-2"));
    }

    @Test
    public void deleteAuthUnknownTokenChangesNothing() throws DataAccessException {
        authDAO.insertAuth(new AuthData("token-1", "morgan"));

        assertDoesNotThrow(() -> authDAO.deleteAuth("no-such-token"));

        assertNotNull(authDAO.getAuth("token-1"));
    }

    @Test
    public void deleteAllRemovesEveryAuth() throws DataAccessException {
        authDAO.insertAuth(new AuthData("token-1", "morgan"));
        authDAO.insertAuth(new AuthData("token-2", "sam"));

        authDAO.deleteAll();

        assertNull(authDAO.getAuth("token-1"));
        assertNull(authDAO.getAuth("token-2"));
    }
}
