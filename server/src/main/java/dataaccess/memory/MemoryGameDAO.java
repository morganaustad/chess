package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextID = 1;
    private final HashMap<Integer, GameData> gameMemory = new HashMap<>();


    @Override
    public int insertGame(GameData game) throws DataAccessException {
        GameData newGame = new GameData(
                nextID++,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
        gameMemory.put(newGame.gameID(), newGame);

        return newGame.gameID();
    }

    @Override
    public GameData getGame(int gameID) {
        return gameMemory.getOrDefault(gameID, null);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameMemory.values();
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        if (gameMemory.containsKey(updatedGame.gameID())) {
            gameMemory.put(updatedGame.gameID(), updatedGame);
        } else {
            throw new DataAccessException("No such game");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        gameMemory.clear();
    }
}
