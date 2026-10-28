package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        DatabaseHelper.initialize();
    }


    @Override
    public void insertUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO user (username, password, email) VALUES (?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setString(1, user.username());
                pstmt.setString(2, user.password());
                pstmt.setString(3, user.email());

                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to add user." + ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }

        } catch (Exception ex) {
            throw new DataAccessException("Error: Unable to get user." + ex);
        }

        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public void deleteAll() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM user";
            try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                pstmt.executeUpdate();
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: unable to delete user table." + ex);
        }
    }
}
