package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    
    public void clearAuth() throws DataAccessException {
        authDAO.clearAuth();
    }

    public String getUsername(String authToken) throws DataAccessException {
        return authDAO.getUser(authToken);
    }

}
