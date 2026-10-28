package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        DatabaseHelper.initialize();
    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        String statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameState) VALUES (?,?,?,?)";
        String gameState = new Gson().toJson(game.game());
        return DatabaseHelper.executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameState);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameId, whiteUsername, blackUsername, gameName, gameState FROM game WHERE gameID=?";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setInt(1, gameID);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Gson gson = new Gson();
                        ChessGame game = gson.fromJson(rs.getString("gameState"), ChessGame.class);

                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to get game." + ex);
        }

        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM game";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    Collection<GameData> gameList = new ArrayList<>();

                    while(rs.next()) {
                        Gson gson = new Gson();
                        ChessGame game = gson.fromJson(rs.getString("gameState"), ChessGame.class);

                        gameList.add(
                                new GameData(
                                        rs.getInt("gameID"),
                                        rs.getString("whiteUsername"),
                                        rs.getString("blackUsername"),
                                        rs.getString("gameName"),
                                        game
                                )
                        );
                    }

                    return gameList;
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to list games." + ex);
        }
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, gameState=? WHERE gameID=?";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setString(1, updatedGame.whiteUsername());
                pstmt.setString(2, updatedGame.blackUsername());
                pstmt.setString(3, updatedGame.gameName());
                Gson gson = new Gson();
                String json = gson.toJson(updatedGame.game());
                pstmt.setString(4, json);
                pstmt.setInt(5, updatedGame.gameID());

                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to update game." + ex);
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM game";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to delete game table." + ex);
        }
    }
}
