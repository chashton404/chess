package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface UserDAO {

    void createUser() throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
