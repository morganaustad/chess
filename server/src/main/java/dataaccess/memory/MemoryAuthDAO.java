package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authMemory;

    public MemoryAuthDAO() {
        authMemory = new HashMap<>(5);
    }

    @Override
    public void insertAuth(AuthData auth) throws DataAccessException {
        authMemory.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authMemory.containsKey(authToken)) {
            return authMemory.get(authToken);
        } else {
            throw new DataAccessException("No valid authToken");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authMemory.containsKey(authToken)) {
            authMemory.remove(authToken);
        } else {
            throw new DataAccessException("No valid authToken");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        authMemory.clear();
    }
}
