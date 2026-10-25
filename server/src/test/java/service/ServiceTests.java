package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private UserService userService;
    private ClearService clearService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(authDAO, userDAO);
        clearService = new ClearService(authDAO, userDAO, gameDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        UserData user = new UserData("morgan", "password", "morgan@example.com");

        AuthData auth = userService.register(user);

        assertEquals("morgan", auth.username());
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isBlank());
        assertEquals(user, userDAO.getUser("morgan"));
        assertEquals(auth, authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void registerAlreadyTaken() throws DataAccessException {
        userService.register(new UserData("morgan", "password", "morgan@example.com"));

        assertThrows(AlreadyTakenException.class,
                () -> userService.register(new UserData("morgan", "other", "other@example.com")));
    }

    @Test
    public void registerBadRequest() {
        assertThrows(BadRequestException.class,
                () -> userService.register(new UserData("morgan", null, "morgan@example.com")));
        assertThrows(BadRequestException.class,
                () -> userService.register(new UserData("morgan", "password", " ")));
        assertNull(userDAO.getUser("morgan"));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        AuthData auth = userService.register(new UserData("morgan", "password", "morgan@example.com"));
        int gameID = gameDAO.insertGame(new GameData(0, null, null, "game", new ChessGame()));

        clearService.clear();

        assertNull(userDAO.getUser("morgan"));
        assertNull(authDAO.getAuth(auth.authToken()));
        assertTrue(gameDAO.listGames().isEmpty());
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameID));
    }
}
