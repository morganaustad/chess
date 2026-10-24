package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final AuthDAO auth;
    private final GameDAO game;
    private final UserDAO user;

    public ClearService(AuthDAO auth, UserDAO user, GameDAO game) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public void clear() throws DataAccessException{
        auth.deleteAll();
        game.deleteAll();
        user.deleteAll();
    }
}
