package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.Data;

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
    void positiveTestCheckAuth() throws DataAccessException{
        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        assertTrue(authDAO.checkAuth("token"), "All positive authTokens exist in the db.");
    }

    @Test
    void negativeTestCheckAuth() {
        assertThrows(DataAccessException.class, () -> authDAO.checkAuth(null), "Null values throw errors");
    }

    @Test
    void positiveTestCheckUser() throws DataAccessException {
        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        assertTrue(authDAO.checkUser("username"), "All existent users should be in the DAO");
    }

    @Test
    void negativeTestCheckUser() {
        assertThrows(DataAccessException.class, () -> authDAO.checkUser(null), "Null values throw errors");
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
    void positiveTestCreateAuth() throws DataAccessException {
        
        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        assertTrue(authDAO.checkAuth("token"), "All positive authTokens exist in the db.");

    }

    @Test
    void negativeTestCreateAuth() throws DataAccessException {

        AuthData testAuth = new AuthData(null, null);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(testAuth), "Creation requests cannot have null fields");

    }

    @Test
    void positiveTestDeleteAuth() throws DataAccessException, UnauthorizedException {

        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());

        assertFalse(authDAO.checkAuth("token"), "All nonexistent authTokens shouldn't exist in the db.");

    }

    @Test
    void negativeTestDeleteAuth() {
        assertThrows(UnauthorizedException.class, () -> authDAO.deleteAuth("5"), "AuthToken must be vaild to logout");
    }

    @Test
    void positiveTestGetUser() throws DataAccessException {
        AuthData testAuth = new AuthData("token", "username");
        authDAO.createAuth(testAuth);
        assertEquals("username", authDAO.getUser("token"), "Get user should retrieve existing users");
    }

    @Test
    void negativeTestGetUser() {
        assertThrows(DataAccessException.class, () -> authDAO.getUser(null), "Null values throw errors");
    }
}
