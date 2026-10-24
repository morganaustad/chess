package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    GameData updateGame(int gameID, ChessGame game);

    void deleteGame(int gameID);

    void deleteAll();
}
