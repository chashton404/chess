package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface GameDAO {
    void createGame() throws DataAccessException;

    void getGame() throws DataAccessException;

    void listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void clearGames() throws DataAccessException;

}