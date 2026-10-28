package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

public class VerifyService {
    public static AuthData verifyAuth(String authToken, AuthDAO authDAO) throws DataAccessException {
        if (
                authToken == null
                || authToken.isBlank()
        ) {
            throw new UnauthorizedException("Unauthorized");
        }

        AuthData checkedAuth = authDAO.getAuth(authToken);

        if (
                checkedAuth == null
                || checkedAuth.authToken() == null
        ) {
            throw new UnauthorizedException("Unauthorized");
        }

        return checkedAuth;
    }
}
