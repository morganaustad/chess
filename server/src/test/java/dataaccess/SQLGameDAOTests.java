package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.sql.DatabaseHelper;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.deleteAll();
    }

    private static GameData newGame(String gameName) {
        return new GameData(0, null, null, gameName, new ChessGame());
    }

    @Test
    public void insertGameReturnsDistinctIds() throws DataAccessException {
        int firstID = gameDAO.insertGame(newGame("first"));
        int secondID = gameDAO.insertGame(newGame("second"));

        assertTrue(firstID > 0);
        assertNotEquals(firstID, secondID);
    }

    @Test
    public void insertGameMissingName() {
        assertThrows(DataAccessException.class, () -> gameDAO.insertGame(newGame(null)));
    }

    @Test
    public void getGameRoundTripsEmptySeats() throws DataAccessException {
        int id = gameDAO.insertGame(newGame("first"));

        GameData found = gameDAO.getGame(id);

        assertEquals(new GameData(id, null, null, "first", new ChessGame()), found);
    }

    @Test
    public void getGameKeepsMovesMade() throws DataAccessException, InvalidMoveException {
        ChessGame played = new ChessGame();
        played.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        int id = gameDAO.insertGame(new GameData(0, "morgan", "sam", "played", played));

        GameData found = gameDAO.getGame(id);

        assertEquals(played, found.game());
        assertEquals(ChessGame.TeamColor.BLACK, found.game().getTeamTurn());
        assertEquals("morgan", found.whiteUsername());
        assertEquals("sam", found.blackUsername());
    }

    @Test
    public void getGameNotFound() throws DataAccessException {
        assertNull(gameDAO.getGame(9999));
    }

    @Test
    public void listGamesReturnsEveryGame() throws DataAccessException {
        gameDAO.insertGame(newGame("first"));
        gameDAO.insertGame(newGame("second"));
        gameDAO.insertGame(newGame("third"));

        Collection<GameData> games = gameDAO.listGames();

        Set<String> names = games.stream().map(GameData::gameName).collect(Collectors.toSet());
        assertEquals(Set.of("first", "second", "third"), names);
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void updateGameSavesPlayerAndMove() throws DataAccessException, InvalidMoveException {
        int id = gameDAO.insertGame(newGame("first"));
        ChessGame played = new ChessGame();
        played.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        GameData updated = new GameData(id, "morgan", null, "first", played);

        gameDAO.updateGame(updated);

        assertEquals(updated, gameDAO.getGame(id));
    }

    @Test
    public void updateGameMissingName() throws DataAccessException {
        int id = gameDAO.insertGame(newGame("first"));

        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(new GameData(id, null, null, null, new ChessGame())));
    }

    @Test
    public void deleteAllRemovesEveryGame() throws DataAccessException {
        int id = gameDAO.insertGame(newGame("first"));
        gameDAO.insertGame(newGame("second"));

        gameDAO.deleteAll();

        assertNull(gameDAO.getGame(id));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    private static int insertCorruptGameRow() throws DataAccessException {
        return DatabaseHelper.executeUpdate(
                "INSERT INTO game (gameName, gameState) VALUES (?, ?)", "corrupt", "not valid json");
    }

    @Test
    public void getGameCorruptState() throws DataAccessException {
        int id = insertCorruptGameRow();

        assertThrows(DataAccessException.class, () -> gameDAO.getGame(id));
    }

    @Test
    public void listGamesCorruptState() throws DataAccessException {
        insertCorruptGameRow();

        assertThrows(DataAccessException.class, () -> gameDAO.listGames());
    }

    @Test
    public void initializeTwiceKeepsExistingData() throws DataAccessException {
        int id = gameDAO.insertGame(newGame("first"));

        DatabaseHelper.initialize();

        assertNotNull(gameDAO.getGame(id));
    }
}

