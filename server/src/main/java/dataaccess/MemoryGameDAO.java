package dataaccess;

import model.GameData;
import java.util.HashMap;

import chess.ChessGame;

public class MemoryGameDAO implements GameDAO {
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    Integer gameID = 1;

    public void clearGames() {
        games.clear();
    }

    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException("Error: Game Name is Null");
        }

        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());

        games.put(gameID, newGame);
        Integer oldGameID = gameID;
        gameID += 1;

        return oldGameID;
    }

    @Override
    public void getGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public void listGames() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void updateGame() throws DataAccessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }
    
}
