package service;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public void clearUsers() throws DataAccessException {
        userDAO.clearUsers();
    }

}
