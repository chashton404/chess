package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.AuthData;
import model.UserData;

public class SQLAuthDAOTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        // Initialize the DAOs
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        
        authDAO.clearAuth();
        userDAO.clearUsers();

        userDAO.createUser(new UserData("username", "password", "example@email.com"));
    }
    
    
    @Test
    void positiveTestCheckAuth() {

    }

    @Test
    void negativeTestCheckAuth() {

    }

    @Test
    void positiveTestCheckUser() {

    }

    @Test
    void negativeTestCheckUser() {

    }

    @Test
    void positiveTestClearAuth() throws DataAccessException {
        // Add someone to the Auth database and then delete them and verify that it's null

        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        authDAO.clearAuth();

        assertFalse(authDAO.checkAuth("token"), "All authTokens shouldn't exist in the db.");
    }

    @Test
    void positiveTestCreateAuth() {

    }

    @Test
    void negativeTestCreateAuth() {

    }

    @Test
    void positiveTestDeleteAuth() {

    }

    @Test
    void negativeTestDeleteAuth() {

    }

    @Test
    void positiveTestGetUser() {

    }

    @Test
    void negativeTestGetUser() {

    }
}
