package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;

import java.util.UUID;

import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import model.AuthData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearUsers() throws DataAccessException {
        userDAO.clearUsers();
    }

    public RegisterResult register(RegisterRequest req) 
        throws DataAccessException, BadRequestException, AlreadyTakenException {
        
        // Make sure that the request is a valid request
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new BadRequestException("Error: bad request");
        }

        // Check to see if there exists a user with the name
        if (userDAO.checkUser(req.username()) == false){
            throw new AlreadyTakenException("Error: already taken");
        }

        // Create a UserData object using the model, and use the request for the constructor
        UserData newUser = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(newUser);

        // Create the authToken using the given code snippet
        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken, req.username());
        authDAO.createAuth(authData);

        return new RegisterResult(req.username(), newAuthToken);
    }
}
