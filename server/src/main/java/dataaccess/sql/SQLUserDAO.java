package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        DatabaseInitializer.initialize();
    }


    @Override
    public void insertUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void deleteAll() throws DataAccessException {

    }
}
