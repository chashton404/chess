package dataaccess;

import java.util.Collection;
import model.ListGameData;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    Collection<ListGameData> listGames() throws DataAccessException;

    Boolean checkGame(Integer gameID) throws DataAccessException;

    Boolean checkColor(Integer gameID, String playerColor) throws DataAccessException;

    void updateGame(Integer gameID, String playerColor, String username) throws DataAccessException;

    void clearGames() throws DataAccessException;

}