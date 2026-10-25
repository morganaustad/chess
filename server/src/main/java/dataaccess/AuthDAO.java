package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void insertAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken) throws DataAccessException;

    void deleteAll() throws DataAccessException;
}
