package service;

import dataaccess.GameDAO;
import dataaccess.DataAccessException;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void clearGames() throws DataAccessException {
        gameDAO.clearGames();
    }
}
