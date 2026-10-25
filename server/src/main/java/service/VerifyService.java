package service;

import dataaccess.AuthDAO;
import model.AuthData;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

public class VerifyService {
    public static void verifyAuth(String authToken, AuthDAO authDAO) {
        if (
                authToken == null
                        || authToken.isBlank()
        ) {
            throw new BadRequestException("Bad AuthData");
        }

        AuthData checkedAuth = authDAO.getAuth(authToken);

        if (
                checkedAuth == null
                        || checkedAuth.authToken() == null
                        || checkedAuth.authToken().isBlank()
                        || checkedAuth.username() == null
                        || checkedAuth.username().isBlank()
        ) {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
