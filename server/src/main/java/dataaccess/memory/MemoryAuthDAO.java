package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    public MemoryAuthDAO() {

    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAll() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
