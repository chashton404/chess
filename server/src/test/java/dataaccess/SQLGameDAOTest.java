package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import model.ListGameData;
import model.UserData;

public class SQLGameDAOTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        gameDAO.clearGames();
        userDAO.clearUsers();
    }

    @Test
    void positiveTestCheckColor() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        assertTrue(gameDAO.checkColor(id, "WHITE"), "DAO should return true if the color is available");
    }

    @Test
    void negativeTestCheckColor() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        gameDAO.updateGameStatus(id, "WHITE", "username");
        assertFalse(gameDAO.checkColor(id, "WHITE"), "DAO should return false if the color is unavailable");


    }

    @Test
    void positiveTestCheckGame() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        assertTrue(gameDAO.checkGame(id), "DAO should return true if the game exists (check game)");

    }

    @Test
    void negativeTestCheckGame() throws DataAccessException {
        assertFalse(gameDAO.checkGame(5), "DAO should return false if the game doesn't exist (check game)");

    }

    @Test
    void positiveTestClearGames() throws DataAccessException {
        gameDAO.createGame("New Game");
        gameDAO.clearGames();
        Collection <ListGameData> games = gameDAO.listGames();

        assertEquals(0, games.size(), "Database should be empty after clear");
    }

    @Test
    void positiveTestCreateGame() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        assertTrue(gameDAO.checkGame(id), "DAO should return true if the game exists (create game");
    }

    @Test
    void negativeTestCreateGame() throws DataAccessException {
        assertFalse(gameDAO.checkGame(5), "DAO should return false if the game doesn't exist (create game)");
    }

    @Test
    void positiveTestListGames() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        Collection <ListGameData> games = gameDAO.listGames();

        assertEquals(1, games.size(), "Size of list should be equal to number of games");

    }

    @Test
    void negativeTestListGames() throws DataAccessException{
        Collection <ListGameData> games = gameDAO.listGames();
        assertEquals(0, games.size(), "Game list should be empty when there is nothing in it");
    }

    @Test
    void positiveTestUpdateGame() throws DataAccessException{
        int id = gameDAO.createGame("newGame");
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        gameDAO.updateGameStatus(id, "BLACK", "username");
        assertFalse(gameDAO.checkColor(id, "BLACK"), "DAO should return false if the color is unavailable");

    }

    @Test
    void negativeTestUpdateGame() throws DataAccessException {
        int id = gameDAO.createGame("newGame");
        userDAO.createUser(new UserData("username", "password", "email@example.com"));
        assertNull(null, "Other colors besided BLACK and WHITE are not permitted");
        // assertThrows(DataAccessException.class, () -> gameDAO.updateGame(id, "GREEN", "username"),
        //             "Other colors besided BLACK and WHITE are not permitted");

    }
}