package dataaccess;

import model.*;

public interface AuthDAO {

    void createAuth(AuthData a) throws DataAccessException;

    String getAuth() throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException;

    void clearAuth() throws DataAccessException;

}
