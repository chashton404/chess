package dataaccess;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    void getGame() throws DataAccessException;

    void listGames() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void clearGames() throws DataAccessException;

}