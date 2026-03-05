package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface UserDAO {

    void createUser(UserData u) throws DataAccessException;

    boolean checkUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
