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
import service.exception.UnauthorizedException;
import service.requestsponse.CreateGameRequest;
import service.requestsponse.GameSummary;
import service.requestsponse.JoinGameRequest;
import service.requestsponse.ListGamesResult;
import service.requestsponse.LoginRequest;

import java.util.Set;
import java.util.stream.Collectors;

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

    private AuthData registerUser(String username) throws DataAccessException {
        return userService.register(new UserData(username, "password", username + "@example.com"));
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
    public void loginSuccess() throws DataAccessException {
        registerUser("morgan");

        AuthData auth = userService.login(new LoginRequest("morgan", "password"));

        assertEquals("morgan", auth.username());
        assertFalse(auth.authToken().isBlank());
        assertEquals(auth, authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void loginUnauthorized() throws DataAccessException {
        registerUser("morgan");

        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("morgan", "wrong")));
        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("nobody", "password")));
    }

    @Test
    public void loginBadRequest() {
        assertThrows(BadRequestException.class,
                () -> userService.login(new LoginRequest("morgan", null)));
        assertThrows(BadRequestException.class, () -> userService.login(null));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        AuthData auth = registerUser("morgan");

        userService.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void logoutUnauthorized() throws DataAccessException {
        AuthData auth = registerUser("morgan");

        assertThrows(UnauthorizedException.class, () -> userService.logout("not-a-token"));
        assertThrows(UnauthorizedException.class, () -> userService.logout(null));
        assertEquals(auth, authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthData auth = registerUser("morgan");

        int gameID = gameService.createGame(auth.authToken(), new CreateGameRequest("game")).gameID();

        GameData stored = gameDAO.getGame(gameID);
        assertNotNull(stored);
        assertEquals("game", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
    }

    @Test
    public void createGameFailures() throws DataAccessException {
        AuthData auth = registerUser("morgan");

        assertThrows(UnauthorizedException.class,
                () -> gameService.createGame("not-a-token", new CreateGameRequest("game")));
        assertThrows(BadRequestException.class,
                () -> gameService.createGame(auth.authToken(), new CreateGameRequest(null)));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        AuthData auth = registerUser("morgan");
        gameService.createGame(auth.authToken(), new CreateGameRequest("first"));
        gameService.createGame(auth.authToken(), new CreateGameRequest("second"));

        ListGamesResult result = gameService.listGames(auth.authToken());

        Set<String> names = result.games().stream().map(GameSummary::gameName).collect(Collectors.toSet());
        assertEquals(2, result.games().size());
        assertEquals(Set.of("first", "second"), names);
    }

    @Test
    public void listGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("not-a-token"));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthData white = registerUser("white");
        AuthData black = registerUser("black");
        int gameID = gameService.createGame(white.authToken(), new CreateGameRequest("game")).gameID();

        gameService.joinGame(white.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID));
        gameService.joinGame(black.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID));

        GameData stored = gameDAO.getGame(gameID);
        assertEquals("white", stored.whiteUsername());
        assertEquals("black", stored.blackUsername());
    }

    @Test
    public void joinGameAlreadyTaken() throws DataAccessException {
        AuthData white = registerUser("white");
        AuthData black = registerUser("black");
        int gameID = gameService.createGame(white.authToken(), new CreateGameRequest("game")).gameID();
        gameService.joinGame(white.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID));
        gameService.joinGame(black.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID));

        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(black.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID)));
        assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(white.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID)));
        assertEquals("white", gameDAO.getGame(gameID).whiteUsername());
        assertEquals("black", gameDAO.getGame(gameID).blackUsername());
    }

    @Test
    public void joinGameFailures() throws DataAccessException {
        AuthData auth = registerUser("morgan");
        int gameID = gameService.createGame(auth.authToken(), new CreateGameRequest("game")).gameID();

        assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame("not-a-token", new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID)));
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(auth.authToken(), new JoinGameRequest(null, gameID)));
        assertThrows(BadRequestException.class,
                () -> gameService.joinGame(auth.authToken(), new JoinGameRequest(ChessGame.TeamColor.WHITE, 999)));
        assertNull(gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        AuthData auth = registerUser("morgan");
        int gameID = gameService.createGame(auth.authToken(), new CreateGameRequest("game")).gameID();

        clearService.clear();

        assertNull(userDAO.getUser("morgan"));
        assertNull(authDAO.getAuth(auth.authToken()));
        assertNull(gameDAO.getGame(gameID));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
