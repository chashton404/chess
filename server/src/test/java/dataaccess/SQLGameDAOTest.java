package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import model.ListGameData;

public class SQLGameDAOTest {
    private final GameDAO gameDAO = new SQLGameDAO();

    @BeforeEach
    void setup() throws DataAccessException {
        gameDAO.clearGames();
    }

    @Test
    void positiveTestCheckColor() {

    }

    @Test
    void negativeTestCheckColor() {

    }

    @Test
    void positiveTestCheckGame() {

    }

    @Test
    void negativeTestCheckGame() {

    }

    @Test
    void positiveTestClearGames() throws DataAccessException {
        gameDAO.createGame("New Game");
        gameDAO.clearGames();
        Collection <ListGameData> games = gameDAO.listGames();

        assertEquals(0, games.size(), "Database should be empty after clear");
    }

    @Test
    void positiveTestCreateGame() {

    }

    @Test
    void negativeTestCreateGame() {

    }

    @Test
    void positiveTestListGames() {

    }

    @Test
    void negativeTestListGames() {

    }

    @Test
    void positiveTestUpdateGame() {

    }

    @Test
    void negativeTestUpdateGame() {

    }
}
