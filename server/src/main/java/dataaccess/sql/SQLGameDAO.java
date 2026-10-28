package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        DatabaseHelper.initialize();
    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData updatedGame) throws DataAccessException {

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
