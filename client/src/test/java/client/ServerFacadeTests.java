package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.requestresponse.CreateGameResult;
import model.requestresponse.GameSummary;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    static void resetAll() throws ResponseException {
        facade.clear();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");

        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    public void registerDuplicate() throws ResponseException {
        facade.register("player", "pass", "em");
        assertThrows(ResponseException.class,
                () -> facade.register("player", "pass", "em"));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        facade.register("player", "pass", "em");
        AuthData auth = facade.login("player", "pass");

        assertEquals("player", auth.username());
    }

    @Test
    public void loginBadPassword() throws ResponseException {
        facade.register("player", "pass", "em");
        assertThrows(ResponseException.class,
                () -> facade.login("player", "oops"));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutBadToken() {
        assertThrows(ResponseException.class, () -> facade.logout("oops"));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        CreateGameResult game = facade.createGame("game1", auth.authToken());
        assertTrue(game.gameID() > 0);
    }

    @Test
    public void createGameNoAuth() {
        assertThrows(ResponseException.class,
                () -> facade.createGame("game1", "oops"));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        facade.createGame("game1", auth.authToken());
        facade.createGame("game2", auth.authToken());

        assertEquals(2, facade.listGames(auth.authToken()).games().toArray().length);
    }

    @Test
    public void listGamesNoAuth() {
        assertThrows(ResponseException.class, () -> facade.listGames("oops"));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        int gameID = facade.createGame("game1", auth.authToken()).gameID();
        facade.joinGame(ChessGame.TeamColor.WHITE, gameID, auth.authToken());
        GameSummary game = facade.listGames(auth.authToken()).games().iterator().next();
        assertEquals("player", game.whiteUsername());
    }

    @Test
    public void joinGameTaken() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        AuthData auth2 = facade.register("player2", "peas", "erm");
        int gameID = facade.createGame("game1", auth.authToken()).gameID();
        facade.joinGame(ChessGame.TeamColor.WHITE, gameID, auth.authToken());

        assertThrows(ResponseException.class,
                () -> facade.joinGame(ChessGame.TeamColor.WHITE, gameID, auth2.authToken()));
    }

    @Test
    public void joinGameBadID() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        assertThrows(ResponseException.class,
                () -> facade.joinGame(ChessGame.TeamColor.WHITE, 9999, auth.authToken()))
    }

    @Test
    public void clearSuccess() throws ResponseException {
        AuthData auth = facade.register("player", "pass", "em");
        facade.logout(auth.authToken());
        facade.clear();

        assertThrows(ResponseException.class,
                () -> facade.login("player", "pass"));
    }

}
