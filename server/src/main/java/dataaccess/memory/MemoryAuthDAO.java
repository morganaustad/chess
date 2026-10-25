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
    public AuthData getAuth(String authToken) {
        return authMemory.getOrDefault(authToken, null);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authMemory.remove(authToken);
    }

    @Override
    public void deleteAll() throws DataAccessException {
        authMemory.clear();
    }
}
