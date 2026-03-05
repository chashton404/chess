package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface AuthDAO {

    void createAuth(AuthData a) throws DataAccessException;

    String getAuth() throws DataAccessException;

    void deleteAuth() throws DataAccessException;

    void clearAuth() throws DataAccessException;

}
