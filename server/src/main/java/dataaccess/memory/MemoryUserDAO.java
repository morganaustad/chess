package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> userMemory;

    public MemoryUserDAO() {
        userMemory = new HashMap<>(5);
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        userMemory.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return userMemory.getOrDefault(username, null);
    }

    @Override
    public void deleteAll() throws DataAccessException {
        userMemory.clear();
    }
}
