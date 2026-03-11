package dataaccess;

import model.*;

public interface AuthDAO {

    void createAuth(AuthData a) throws DataAccessException;

    Boolean checkAuth(String authToken) throws DataAccessException;

    String getUser(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException, UnauthorizedException;

    void clearAuth() throws DataAccessException;

}
