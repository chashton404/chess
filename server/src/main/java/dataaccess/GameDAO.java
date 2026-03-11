package dataaccess;

import java.util.Collection;

import model.GameData;
import model.ListGameData;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    void getGame() throws DataAccessException;

    Collection<ListGameData> listGames() throws DataAccessException;

    Boolean checkGame(Integer gameID) throws DataAccessException;

    void updateGame() throws DataAccessException;

    void clearGames() throws DataAccessException;

}