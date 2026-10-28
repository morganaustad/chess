package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        DatabaseHelper.initialize();
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statemtent = "INSERT INTO auth (authToken, username) VALUES (?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(statemtent)) {
                pstmt.setString(1, auth.authToken());
                pstmt.setString(2, auth.username());

                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to insert auth." + ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statemtent = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (PreparedStatement pstmt = conn.prepareStatement(statemtent)) {
                pstmt.setString(1, authToken);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to get auth." + ex);
        }

        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statemtent = "DELETE FROM auth WHERE authToken=?";
            try (PreparedStatement pstmt = conn.prepareStatement(statemtent)) {
                pstmt.setString(1, authToken);
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to insert auth." + ex);
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM auth";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to delete auth table." + ex);
        }
    }
}
