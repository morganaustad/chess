package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;

import java.util.UUID;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (
                user == null
                || user.username() == null
                || user.password() == null
                || user.email() == null
                || user.username().isBlank()
                || user.password().isBlank()
                || user.email().isBlank()
        ) {
            throw new BadRequestException("Bad Request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username Already Taken");
        }

        userDAO.insertUser(user);

        return createAuth(user.username());
    }

    private AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.insertAuth(auth);
        return auth;
    }
}
