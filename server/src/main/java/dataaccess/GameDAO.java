package dataaccess;

import dataaccess.DataAccessException;
import model.*;

public interface GameDAO {
    void clear() throws DataAccessException;

    void createUser() throws DataAccessException;

    //These are some of the functions that we may want to implement:
    //createGame, getGame, listGames, updateGame
}