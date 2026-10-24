package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import service.result.ClearResult;

public class ClearService {
    AuthDAO auth;
    GameDAO game;
    UserDAO user;

    public ClearService() {
        auth = new MemoryAuthDAO();
        game = new MemoryGameDAO();
        user = new MemoryUserDAO();
    }

    public ClearResult clear() {
        try {
            auth.deleteAll();
            game.deleteAll();
            user.deleteAll();
        } catch (DataAccessException e) {
            return new ClearResult(500, "Error: " + e);
        }

        return new ClearResult(200, "Everything has been deleted.");
    }
}
