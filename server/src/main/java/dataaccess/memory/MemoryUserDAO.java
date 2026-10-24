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
    public UserData getUser(String username) throws DataAccessException {
        if (userMemory.containsKey(username)) {
            return userMemory.get(username);
        } else {
            throw new DataAccessException("No such user");
        }
    }

    @Override
    public void deleteAll() throws DataAccessException {
        userMemory.clear();
    }
}
