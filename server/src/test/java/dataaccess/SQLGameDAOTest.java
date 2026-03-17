package dataaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
