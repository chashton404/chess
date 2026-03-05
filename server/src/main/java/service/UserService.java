package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;

import java.util.UUID;

import model.RegisterRequest;
import model.LoginRequest;
import model.LoginResult;
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

    public LoginResult register(RegisterRequest req) 
        throws DataAccessException, BadRequestException, AlreadyTakenException {
        
        // Make sure that the request is a valid request
        if (req.username() == null || req.password() == null || req.email() == null){
            throw new BadRequestException("Error: bad request");
        }

        // Check to see if there exists a user with the name
        if (userDAO.checkUser(req.username()) == true){
            throw new AlreadyTakenException("Error: already taken");
        }

        // Create a UserData object using the model, and use the request for the constructor
        UserData newUser = new UserData(req.username(), req.password(), req.email());
        userDAO.createUser(newUser);

        // Create the authToken using the given code snippet
        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken, req.username());
        authDAO.createAuth(authData);

        return new LoginResult(req.username(), newAuthToken);
    }

    public LoginResult login(LoginRequest req) 
        throws DataAccessException, BadRequestException, UnauthorizedException {

        // The first thing that we do here is we check if they filled in the username and the password
        if (req.username() == null || req.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        // Next we check to see if their username is in the database
        if (userDAO.checkUser(req.username()) == false) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        // Now we make sure that the password matches the db
        if (!userDAO.getUser(req.username()).password().equals(req.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        
        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken, req.username());
        authDAO.createAuth(authData);

        return new LoginResult(req.username(), newAuthToken);
    }

}
