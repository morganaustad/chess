package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;
import service.requestsponse.LoginRequest;

import java.util.UUID;

import static service.VerifyService.verifyAuth;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
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

        UserData cryptUser = new UserData(
                user.username(),
                BCrypt.hashpw(user.password(), BCrypt.gensalt()),
                user.email()
        );
        userDAO.insertUser(cryptUser);

        return createAuth(cryptUser.username());
    }

    private AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.insertAuth(auth);
        return auth;
    }

    public AuthData login(LoginRequest loginRequest) throws UnauthorizedException, BadRequestException, DataAccessException {
        if (
               loginRequest == null
               || loginRequest.username() == null
               || loginRequest.username().isBlank()
               || loginRequest.password() == null
               || loginRequest.password().isBlank()
        ) {
            throw new BadRequestException("Bad Request");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        if (
                user == null
                || !user.username().equals(loginRequest.username())
                || !BCrypt.checkpw(loginRequest.password(), user.password())
        ) {
            throw new UnauthorizedException("Unauthorized");
        }

        return createAuth(loginRequest.username());
    }

    public void logout(String authToken) throws DataAccessException {
        verifyAuth(authToken, authDAO);
        authDAO.deleteAuth(authToken);
    }
}
