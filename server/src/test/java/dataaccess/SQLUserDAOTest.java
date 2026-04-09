package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

import model.UserData;

public class SQLUserDAOTest {
    
    private UserDAO userDAO;
    
    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clearUsers();
    }
    
    @Test
    void positiveTestCheckUser() throws DataAccessException {
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        assertTrue(userDAO.checkUser("username"), "Users should exist in database after creating it");

    }

    @Test
    void negativeTestCheckUser() throws DataAccessException {
        UserData result = userDAO.getUser("Fake user");
        assertNull(result.username(), "DAO should return null upon request for nonexistent user");
    }

    @Test
    void positiveTestClearUsers() throws DataAccessException {
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        userDAO.clearUsers();

        assertFalse(userDAO.checkUser("username"), "Users Database should be empty after clearing it");

    }

    @Test
    void positiveTestCreateUser() throws DataAccessException {
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        assertTrue(userDAO.checkUser("username"), "Users should exist in database after creating it");

    }

    @Test
    void negativeTestCreateUser() throws DataAccessException{
        userDAO.createUser(new UserData("existingUser", "password", "email1@gmail.com"));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData("existingUser", "passw0rd", "email@gmail.com")));
    }

    @Test
    void positiveTestGetUser() throws DataAccessException{
        UserData newUser = new UserData("username", "password", "email@example.com");
        userDAO.createUser(newUser);
        assertTrue(BCrypt.checkpw(newUser.password(), userDAO.getUser(newUser.username()).password()), "Existent users should be in the DAO");
    }

    @Test
    void negativeTestGetUser() throws DataAccessException{
        assertNull(userDAO.getUser("username").username(), "Non-existent users should return null");
    }
}
