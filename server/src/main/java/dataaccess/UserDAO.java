package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface UserDAO {

    void createUser() throws DataAccessException;

    boolean checkUser() throws DataAccessException;

    void clearUsers() throws DataAccessException;

}
