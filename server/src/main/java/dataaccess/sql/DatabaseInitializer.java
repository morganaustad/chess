package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseInitializer {
    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL
            )
            """,

            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                gameState TEXT NOT NULL
            )
            """
    };

    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Unable to create tables: " + ex);
        }
    }
}
